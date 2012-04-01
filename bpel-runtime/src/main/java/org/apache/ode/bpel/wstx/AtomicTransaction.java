package org.apache.ode.bpel.wstx;

import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.arjuna.mw.wscf.model.twophase.api.UserCoordinator;
import com.arjuna.mw.wscf11.model.twophase.UserCoordinatorFactory;
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

  protected UserTransaction _tx;
  protected boolean active;
  
  public AtomicTransaction(){
    active = false;
  }
  
  public void begin() throws WrongStateException, SystemException {
    _tx = UserTransaction.getUserTransaction();
    if(_tx == null)
      throw new SystemException("Distributed transaction has not been created. Check that JBoss XTS is runnning.");
    _tx.begin();
    active = true;
  }

  public void commit() throws SecurityException, TransactionRolledBackException, UnknownTransactionException, SystemException, WrongStateException {
    active = false;
    _tx.commit();
  }

  public boolean isActive() {
    return _tx != null && active;
  }
  
  public String getTransactionIdentifier(){
    return _tx.transactionIdentifier();
  }

  public void rollback() throws SecurityException, UnknownTransactionException, SystemException, WrongStateException {
    active = false;
    _tx.rollback();
  }

  public Element putCoordinationContext(Element headerElement) throws SystemException {
    final TxContextImple txContext = (TxContextImple)TransactionManager.getTransactionManager().currentTransaction();
    CoordinationContextType ctx = txContext.context().getCoordinationContext();
    try{
      Document doc = headerElement.getOwnerDocument();
      Element coord = doc.createElementNS(CoordinationConstants.WSCOOR_NAMESPACE, CoordinationConstants.WSCOOR_ELEMENT_COORDINATION_CONTEXT);
      headerElement.appendChild(coord);
      CoordinationContextHelper.serialise(ctx, headerElement);
    }catch (Exception e) {
      e.printStackTrace();
      throw new SystemException("Coordination context has not been added to header.");
    }
    return headerElement;
  }

}
