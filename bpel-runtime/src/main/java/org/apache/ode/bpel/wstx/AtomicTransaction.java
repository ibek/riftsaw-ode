package org.apache.ode.bpel.wstx;

import org.apache.ode.utils.DOMUtils;
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

public class AtomicTransaction extends WebServiceTransaction {

  protected UserTransaction _tx;
  protected UserCoordinator _userCoord;
  
  protected AtomicTransaction(){
    
  }
  
  public void begin() throws WrongStateException, SystemException {
    _tx = UserTransaction.getUserTransaction();
    if(_tx == null)
      throw new SystemException("Distributed transaction has not been created. Check that JBoss XTS is runnning.");
    _tx.begin();
    try{
      _userCoord = UserCoordinatorFactory.userCoordinator();
      _userCoord.begin("");
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void commit() throws SecurityException, TransactionRolledBackException, UnknownTransactionException, SystemException, WrongStateException {
    _tx.commit();
  }

  public boolean isActive() {
    return _tx != null;
  }
  
  public String getTransactionIdentifier(){
    return _tx.transactionIdentifier();
  }

  public void rollback() throws SecurityException, UnknownTransactionException, SystemException, WrongStateException {
    _tx.rollback();
  }

  @Override
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
