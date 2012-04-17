package org.apache.ode.bpel.wstx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.engine.MessageImpl;
import org.apache.ode.bpel.iapi.Message;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.arjuna.mw.wst.TxContext;
import com.arjuna.mw.wst11.TransactionManager;
import com.arjuna.mw.wst11.UserTransaction;
import com.arjuna.mw.wst11.common.CoordinationContextHelper;
import com.arjuna.mwlabs.wst11.at.context.TxContextImple;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;

public class AtomicTransaction implements WebServiceTransaction {

    private static final Log __log = LogFactory.getLog(AtomicTransaction.class);

    protected UserTransaction _tx;
    protected TxContext _txcontext;
    protected boolean _active;

    public AtomicTransaction() {
        _active = false;
    }

    /**
     * Begin of transaction must be performed with mutual exclusion in one thread
     * because the registration service cannot begin more transactions in one time.
     */
    private static synchronized void begin(UserTransaction tx) throws WrongStateException, SystemException{
        tx.begin();
    }
    
    public void begin(Message bpelRequest) throws WrongStateException, SystemException {
        MessageImpl req = (MessageImpl)bpelRequest;
        boolean subordinate = false;
        if(req._dao.getHeader() != null){
            try {
                CoordinationContextType cct = CoordinationContextHelper.deserialise(req._dao.getHeader());
                if (cct != null) {
                    TxContext ctx = new TxContextImple(cct);
                    TransactionManager.getTransactionManager().resume(ctx);
                    subordinate = true;
                }
            } catch (Exception e) {
                __log.error("Wrong coordination context, the transaction won't be subordinated.");
            }
        }
        
        _tx = UserTransaction.getUserTransaction();
        if (subordinate && _tx != null) {
            _tx = UserTransaction.getUserTransaction().getUserSubordinateTransaction();
        }
        
        if (_tx == null)
            throw new SystemException(
                    "Distributed transaction has not been created. Check that JBoss XTS is runnning.");
        try {
            begin(_tx);
        } catch (WrongStateException wse) {
            TransactionManager.getTransactionManager().suspend(); // previous transaction will be resumed by another instance
            _tx = UserTransaction.getUserTransaction();
            begin(_tx); // we try again to create new transaction
        }
        _txcontext = TransactionManager.getTransactionManager().currentTransaction();
        _active = true;
    }

    public void commit() throws SecurityException, UnknownTransactionException, SystemException,
            WrongStateException {
        _active = false;
        try {
            resume();
            _tx.commit();
        } catch (TransactionRolledBackException e) {
            __log.info("Web service transaction was aborted");
        } finally {
            _tx = null;
            _txcontext = null;
        }
    }

    public boolean isActive() {
        return _tx != null && _active;
    }

    public String getTransactionIdentifier() {
        return _tx.transactionIdentifier();
    }

    public void rollback() throws SecurityException, UnknownTransactionException, SystemException,
            WrongStateException {
        _active = false;
        try{
            resume();
            _tx.rollback();
        } finally {
            _tx = null;
            _txcontext = null;
        }
    }

    public void resume() throws UnknownTransactionException, SystemException {
        if (!_txcontext.equals(TransactionManager.getTransactionManager().currentTransaction())) {
            TransactionManager.getTransactionManager().resume(_txcontext);
            _tx = UserTransaction.getUserTransaction();
            __log.info("Transaction " + _tx.transactionIdentifier() + " resumed.");
        }
    }

    public void suspend() throws SystemException {
        _txcontext = TransactionManager.getTransactionManager().suspend();
        __log.info("Transaction suspended.");
    }

    public Element putCoordinationContext(Element headerElement) throws UnknownTransactionException, SystemException {
        __log.info("Context with ID " + _tx.transactionIdentifier());
        resume();
        final TxContextImple txContext = (TxContextImple) _txcontext;
        CoordinationContextType ctx = txContext.context().getCoordinationContext();
        try {
            Document doc = headerElement.getOwnerDocument();
            Element coord = doc.createElementNS(CoordinationConstants.WSCOOR_NAMESPACE,
                    CoordinationConstants.WSCOOR_ELEMENT_COORDINATION_CONTEXT);
            headerElement.appendChild(coord);
            CoordinationContextHelper.serialise(ctx, headerElement);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException("Coordination context has not been added to header.");
        }
        return headerElement;
    }

}
