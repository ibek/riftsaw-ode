/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ode.bpel.engine;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.transaction.TransactionManager;

import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.ode.bpel.engine.MyRoleMessageExchangeImpl.ResponseCallback;
import org.apache.ode.dao.bpel.MessageExchangeDAO;
import org.apache.ode.scheduler.simple.SimpleScheduler;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class MyRoleMessageExchangeImplTest extends MockObjectTestCase {
    private Mock mexDao;
    
    private TestMyRoleMessageExchangeImpl myRoleMexImpl;
    Contexts contexts;
    BpelEngineImpl engine;
    TransactionManager _txm;
    
    public void testResponseReceived() throws Exception {
        mexDao.expects(exactly(3)).method("getCorrelationId").will(returnValue("corrId"));
        
        final boolean[] responded = new boolean[1];
        myRoleMexImpl.callbacks().put("corrId", new ResponseCallback() {
            synchronized boolean responseReceived() {
                responded[0] = true;
                return true;
            }

            synchronized void waitResponse(long timeout) {
            }
        });
        
        _txm.begin();
        myRoleMexImpl.responseReceived();
        _txm.rollback();
        
        _txm.begin();
        myRoleMexImpl.responseReceived();
        _txm.rollback();
        
        _txm.begin();
        myRoleMexImpl.responseReceived();
        _txm.commit();
        
        assertTrue(responded[0]);
    }
    
    public void testResponseTimeout() throws Exception {
        mexDao.expects(atLeastOnce()).method("getCorrelationId").will(returnValue("corrId"));
        myRoleMexImpl.callbacks().put("corrId", new MyRoleMessageExchangeImpl.ResponseCallback());

        _txm.begin();
        myRoleMexImpl.responseReceived();
        _txm.rollback();

        try {
            new MyRoleMessageExchangeImpl.ResponseFuture("corrId").get(10, TimeUnit.MILLISECONDS);
            fail("Should throw a TimeoutException!!");
        } catch( TimeoutException te ) {}
    }
    
    protected void setUp() throws Exception {
        _txm = new GeronimoTransactionManager();
        
        mexDao = new Mock(MessageExchangeDAO.class);
        SimpleScheduler scheduler = new SimpleScheduler("node", null, _txm, new Properties());
        
        contexts = new Contexts();
        contexts.scheduler = scheduler;
        engine = new BpelEngineImpl(contexts, new Properties());

        myRoleMexImpl = new TestMyRoleMessageExchangeImpl();
    }

    class TestMyRoleMessageExchangeImpl extends MyRoleMessageExchangeImpl {
        public TestMyRoleMessageExchangeImpl() {
            super(null, engine, (MessageExchangeDAO)mexDao.proxy());
        }
        
        public Map<String, ResponseCallback> callbacks() {
            return _waitingCallbacks;
        }
    }
}
