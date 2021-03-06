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

package org.apache.ode.dao.jpa.store;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Matthieu Riou <mriou at apache dot org>
 */
@Entity
@Table(name="STORE_PROCESS_PROP")
public class ProcessConfPropertyDaoImpl {
	
	@Id @Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _id;
	
    @Basic @Column(name="PROP_KEY")
    private String _key;
    @Basic @Column(name="PROP_VAL")
    private String _value;

    public String getKey() {
        return _key;
    }

    public void setKey(String key) {
        _key = key;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }
    
    /**
     * this is only for hibernate custom sequence usage.
     */
    @Deprecated
	public Long get_id() {
		return _id;
	}
	
    /**
     * @deprecated, this is only for hibernate custom sequence usage.
     */
    @Deprecated
	public void set_id(Long id) {
		_id = id;
	}
}
