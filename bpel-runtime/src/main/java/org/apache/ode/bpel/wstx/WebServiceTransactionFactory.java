package org.apache.ode.bpel.wstx;

public class WebServiceTransactionFactory {

    public static WebServiceTransaction instance(int type) {
        switch(type){
            case WebServiceTransaction.ATOMIC_TRANSACTION:
                return new AtomicTransaction();
            case WebServiceTransaction.BUSINESS_ACTIVITY_ATOMIC_OUTCOME:
                return new BusinessActivity(WebServiceTransaction.BUSINESS_ACTIVITY_ATOMIC_OUTCOME);
            case WebServiceTransaction.BUSINESS_ACTIVITY_MIXED_OUTCOME:
                return new BusinessActivity(WebServiceTransaction.BUSINESS_ACTIVITY_MIXED_OUTCOME);
            default:
                return null;
        }
    }
    
}
