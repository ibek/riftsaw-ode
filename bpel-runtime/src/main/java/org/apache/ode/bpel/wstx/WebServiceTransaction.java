package org.apache.ode.bpel.wstx;

import org.w3c.dom.Element;

import com.arjuna.wst.SystemException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;

public interface WebServiceTransaction {
  
  /**
   * TODO: begin transaction should have specified timeout
   * @throws WrongStateException
   * @throws SystemException
   */
  public void begin() throws WrongStateException, SystemException;
  
  public void commit() throws SecurityException, UnknownTransactionException, SystemException, WrongStateException;
  
  public boolean isActive();
  
  public void rollback() throws SecurityException, UnknownTransactionException, SystemException, WrongStateException;
  
  public String getTransactionIdentifier();
  
  public void resume() throws UnknownTransactionException, SystemException;
  
  public void suspend() throws SystemException;
  
  public Element putCoordinationContext(Element headerElement) throws UnknownTransactionException, SystemException;
  
}
