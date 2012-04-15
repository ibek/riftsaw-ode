package org.apache.ode.bpel.wstx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.arjuna.mw.wst11.BusinessActivityManager;
import com.arjuna.mw.wst11.UserBusinessActivity;
import com.arjuna.mw.wst11.common.CoordinationContextHelper;
import com.arjuna.mwlabs.wst11.at.context.TxContextImple;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;

public class BusinessActivity implements WebServiceTransaction {

    private static final Log __log = LogFactory.getLog(BusinessActivity.class);

    protected UserBusinessActivity _uba;
    protected boolean active;

    public BusinessActivity() {
        active = false;
    }

    public void begin() throws WrongStateException, SystemException {
        _uba = UserBusinessActivity.getUserBusinessActivity();
        if (_uba == null)
            throw new SystemException(
                    "Distributed transaction has not been created. Check that JBoss XTS is runnning.");
        _uba.begin();
        active = true;
    }

    public void commit() throws SecurityException, UnknownTransactionException, SystemException,
            WrongStateException {
        active = false;
        _uba.cancel();
    }

    public boolean isActive() {
        return _uba != null && active;
    }

    public void rollback() throws SecurityException, UnknownTransactionException, SystemException,
            WrongStateException {
        active = false;
        _uba.cancel();
    }

    public String getTransactionIdentifier() {
        return _uba.transactionIdentifier();
    }

    public void resume() throws UnknownTransactionException, SystemException {
        // TODO Auto-generated method stub

    }

    public void suspend() throws SystemException {
        // TODO Auto-generated method stub

    }

    public Element putCoordinationContext(Element headerElement)
            throws UnknownTransactionException, SystemException {
        final TxContextImple txContext = (TxContextImple) BusinessActivityManager.getBusinessActivityManager().currentTransaction();
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
