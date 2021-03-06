/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.synapse.transport.dynamicconfigurations;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.ParameterInclude;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.commons.jmx.MBeanRegistrar;
import org.apache.synapse.transport.dynamicconfigurations.jmx.SSLProfileInvoker;

/**
 * Profile re-loader for nhttp and pass-through SSL receivers
 */
public class ListenerProfileReloader extends DynamicProfileReloader {

    private static final Log LOG = LogFactory.getLog(ListenerProfileReloader.class);

    private SSLProfileLoader sslProfileLoader;
    private ParameterInclude transportInDescription;
    private SSLProfileInvoker sslProfileInvoker;

    public ListenerProfileReloader(SSLProfileLoader profileLoader,
                                   ParameterInclude transportInDescription) {
        this.sslProfileLoader = profileLoader;
        this.transportInDescription = transportInDescription;

        if (registerListener(this.transportInDescription)) {
            this.sslProfileInvoker = new SSLProfileInvoker(this);
            MBeanRegistrar.getInstance().registerMBean(sslProfileInvoker, "ListenerSSLProfileReloader",
                                                       getClassName(sslProfileLoader.getClass().getName()));
        }
    }

    /**
     * Notification method which will be triggered by FileUpdateNotifier
     */
    public void notifyFileUpdate() {
        try {
            sslProfileLoader.reloadConfig(transportInDescription);
        } catch (AxisFault axisFault) {
            LOG.error("Error reloading dynamic SSL configurations for Listeners : New Configurations will not be applied "
                      + axisFault.getMessage());
        }
    }



}