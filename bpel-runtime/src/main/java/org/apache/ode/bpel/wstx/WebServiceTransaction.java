package org.apache.ode.bpel.wstx;

import org.w3c.dom.Element;

import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;

public interface WebServiceTransaction {
  
  public void begin() throws WrongStateException, SystemException;
  
  public void commit() throws SecurityException, TransactionRolledBackException, UnknownTransactionException, SystemException, WrongStateException;
  
  public boolean isActive();
  
  public void rollback() throws SecurityException, UnknownTransactionException, SystemException, WrongStateException;
  
  public String getTransactionIdentifier();
  
  public Element putCoordinationContext(Element headerElement) throws SystemException;
  
}
