/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.startupcheck.testcheck.impl;

import org.apache.sling.startupcheck.core.OsgiInstallerCheck;
import org.apache.sling.startupcheck.core.StartupCheck;
import org.apache.sling.startupcheck.testcheck.TestCheck;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@org.osgi.service.component.annotations.Component(
        name = "Test Startup Check ",
        service = {TestCheck.class, StartupCheck.class},
        configurationPid = "org.apache.sling.startupcheck.testcheck.TestCheckImpl"
)
public class TestCheckImpl implements TestCheck {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private long startedTime = -1;
    private OsgiInstallerCheck osgiInstallerCheck;

    @Activate
    protected void activate(final BundleContext ctx, final Map<String, Object> properties) throws InterruptedException {
        log.info("Activating");

        // get reference to OsgiInstallerCheck
        final ServiceReference<OsgiInstallerCheck> osgiInstallerRef = ctx.getServiceReference(OsgiInstallerCheck.class);
        this.osgiInstallerCheck = ctx.getService(osgiInstallerRef);

        log.info("Activated!");
    }

    @Deactivate
    protected void deactivate() throws InterruptedException {
       this.startedTime = -1;
       this.osgiInstallerCheck = null;
    }

    @Override
    public boolean isStarted() {
        log.info("Checking if we're started");

        if (null == osgiInstallerCheck || !osgiInstallerCheck.isStarted()) {
            log.info("OSGI installation hasn't finished yet");
            return false;
        } else {
            // start timer;
            if (this.startedTime < 0) {
                this.startedTime = System.currentTimeMillis();
                log.info("Started timer: {}", this.startedTime);
            }

            long elapsed = System.currentTimeMillis() - this.startedTime;

            if (elapsed < 20000) {
                log.info("We're not started yet! Elapsed: {}", elapsed);
                return false;
            } else {
                log.info("We're STARTED! Elapsed: {}", elapsed);
                return true;
            }
        }
    }

    @Override
    public String getStatus() {
        long elapsed = System.currentTimeMillis() - this.startedTime;
        String status = "Working hard! ";
        if (this. startedTime > 0) {
            status +=  "Elapsed: " + elapsed;
        } else {
            status += "Waiting for Osgi installation.";
        }
        return status;
    }
}
