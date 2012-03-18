package org.apache.ode.bpel.wstx;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;

import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;

public abstract class WebServiceTransaction {
  
  public abstract void begin() throws WrongStateException, SystemException;
  
  public abstract void commit() throws SecurityException, TransactionRolledBackException, UnknownTransactionException, SystemException, WrongStateException;
  
  public abstract boolean isActive();
  
  public abstract void rollback() throws SecurityException, UnknownTransactionException, SystemException, WrongStateException;
  
  public abstract String getTransactionIdentifier();
  
  public abstract Element putCoordinationContext(Element headerElement) throws SystemException;
  
  @SuppressWarnings({"unchecked"})
  public static WebServiceTransaction checkOperation(Operation operation, Collection<Binding> bindings){
    Iterator<Binding> i = bindings.iterator();
    while(i.hasNext()){
      Binding b = i.next();
      List<BindingOperation> bolist = b.getBindingOperations();
      for(BindingOperation bo : bolist){
        if(bo.getOperation().getName().compareTo(operation.getName()) != 0)
          continue;
        List<ExtensibilityElement> eelist = bo.getExtensibilityElements();
        for(ExtensibilityElement ee : eelist){
          if (! (ee instanceof UnknownExtensibilityElement))
            continue;
          UnknownExtensibilityElement uee = (UnknownExtensibilityElement) ee;
          if(uee.getElementType().getLocalPart().equals("PolicyReference")){
            String uri = uee.getElement().getAttribute("URI").substring(1);
            NodeList policyNodeList = uee.getElement().getOwnerDocument().getElementsByTagNameNS("*", "Policy");
            for (int j = 0; j < policyNodeList.getLength(); j++) {
                Element element = (Element) policyNodeList.item(j);
                String refUri = element.getAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
                if (refUri != null && refUri.equals(uri)) {
                    NodeList wsat = element.getElementsByTagNameNS("http://docs.oasis-open.org/ws-tx/wsat/2006/06","ATAssertion");
                    if(wsat != null && wsat.getLength() == 1){
                      return new AtomicTransaction();
                    }
                    return null;
                }
            }
          }
        }
      }
    }
    return null;
  }
  
}
