package org.apache.ode.bpel.wstx;

public class WebServiceTransactionFactory {

    public static WebServiceTransaction instance(WebServiceTransactionType type) {
        switch(type){
            case ATOMIC_TRANSACTION:
                return new AtomicTransaction();
            case BUSINESS_ACTIVITY_ATOMIC_OUTCOME:
                return new BusinessActivity(WebServiceTransactionType.BUSINESS_ACTIVITY_ATOMIC_OUTCOME);
            case BUSINESS_ACTIVITY_MIXED_OUTCOME:
                return new BusinessActivity(WebServiceTransactionType.BUSINESS_ACTIVITY_MIXED_OUTCOME);
            default:
                return null;
        }
    }
    
}
