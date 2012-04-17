package org.apache.ode.bpel.wstx;

import org.apache.ode.bpel.iapi.Message;
import org.w3c.dom.Element;

import com.arjuna.wst.SystemException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;

public interface WebServiceTransaction {

    public static final int NOT_DETERMINED = 0;
    public static final int ATOMIC_TRANSACTION = 1;
    public static final int BUSINESS_ACTIVITY_ATOMIC_OUTCOME = 2;
    public static final int BUSINESS_ACTIVITY_MIXED_OUTCOME = 3;
    
    /**
     * TODO: begin transaction should have specified timeout
     * 
     * @throws WrongStateException
     * @throws SystemException
     */
    public void begin(Message bpelRequest) throws WrongStateException, SystemException;

    public void commit() throws SecurityException, UnknownTransactionException, SystemException,
            WrongStateException;

    public boolean isActive();

    public void rollback() throws SecurityException, UnknownTransactionException, SystemException,
            WrongStateException;

    public String getTransactionIdentifier();

    public void resume() throws UnknownTransactionException, SystemException;

    public void suspend() throws SystemException;

    public Element putCoordinationContext(Element headerElement)
            throws UnknownTransactionException, SystemException;

}
