package org.apache.ode.bpel.wstx;

import com.arjuna.mw.wst11.UserTransaction;
import com.arjuna.mw.wst11.UserTransactionFactory;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;

public class AtomicTransaction extends DistributedTransaction {

  protected UserTransaction _tx;
  
  protected AtomicTransaction(){
    
  }
  
  public void begin() throws WrongStateException, SystemException {
    _tx = UserTransactionFactory.userTransaction();
    _tx.begin();
  }

  public void commit() throws SecurityException, TransactionRolledBackException, UnknownTransactionException, SystemException, WrongStateException {
    _tx.commit();
  }

  public boolean isActive() {
    return _tx != null;
  }

  public void rollback() throws SecurityException, UnknownTransactionException, SystemException, WrongStateException {
    _tx.rollback();
  }

}
