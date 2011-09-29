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
package org.apache.ode.dao.jpa.hibernate;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.dao.jpa.JpaOperator;
import org.apache.ode.dao.jpa.scheduler.SchedulerDAOConnectionImpl;
import org.apache.ode.dao.scheduler.SchedulerDAOConnection;
import org.apache.ode.dao.scheduler.SchedulerDAOConnectionFactory;
import org.apache.ode.il.config.OdeConfigProperties;

/**

 */
public class SchedulerDAOConnectionFactoryImpl implements SchedulerDAOConnectionFactory {

    static final Log __log = LogFactory.getLog(SchedulerDAOConnectionFactoryImpl.class);
    EntityManagerFactory _emf;
    TransactionManager _txm;
    DataSource _ds;
    JpaOperator _operator = new JpaOperatorImpl();

    public void init(Properties odeConfig, TransactionManager txm, Object env) {
        this._txm = txm;
        this._ds = (DataSource) env;
        Map emfProperties = HibernateUtil.buildConfig(OdeConfigProperties.PROP_DAOCF_SCHEDULER + ".", odeConfig, _txm, _ds);
        _emf = Persistence.createEntityManagerFactory("ode-scheduler", emfProperties);

    }

    public SchedulerDAOConnection getConnection() {
        final ThreadLocal<SchedulerDAOConnectionImpl> currentConnection = SchedulerDAOConnectionImpl.getThreadLocal();

        SchedulerDAOConnectionImpl conn = (SchedulerDAOConnectionImpl) currentConnection.get();
        if (conn != null && HibernateUtil.isOpen(conn)) {
            return conn;
        } else {
            EntityManager em = _emf.createEntityManager();
            conn = new SchedulerDAOConnectionImpl(em, _txm, _operator);
            currentConnection.set(conn);
            return conn;
        }
    }

    public void shutdown() {
        _emf.close();
    }
}
