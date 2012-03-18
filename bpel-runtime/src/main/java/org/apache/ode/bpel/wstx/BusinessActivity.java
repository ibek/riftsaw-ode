package org.apache.ode.bpel.wstx;

import org.w3c.dom.Element;

import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;

public class BusinessActivity extends WebServiceTransaction {
  
  protected BusinessActivity(){
    
  }

  public void begin() throws WrongStateException, SystemException{
    // TODO Auto-generated method stub
    
  }

  public void commit() throws SecurityException, TransactionRolledBackException, UnknownTransactionException, SystemException, WrongStateException{
    // TODO Auto-generated method stub
    
  }

  public boolean isActive() {
    // TODO Auto-generated method stub
    return false;
  }

  public void rollback() throws SecurityException, UnknownTransactionException, SystemException, WrongStateException{
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getTransactionIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Element putCoordinationContext(Element headerElement) throws SystemException {
    // TODO Auto-generated method stub
    return null;
  }

}
