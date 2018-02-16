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

import org.apache.sling.startupcheck.core.StartupCheck;
import org.apache.sling.startupcheck.testcheck.TestCheck;
import org.osgi.framework.BundleContext;
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
    private long startedTime;

    @Activate
    protected void activate(final BundleContext ctx, final Map<String, Object> properties) throws InterruptedException {
        log.info("Activating");
        Thread.sleep(5000);
        this.startedTime = System.currentTimeMillis();
        log.info("Activated!");
    }

    @Deactivate
    protected void deactivate() throws InterruptedException {
       this.startedTime = -1;
    }

    @Override
    public boolean isStarted() {
        log.info("Checking if we're started");
        long elapsed = System.currentTimeMillis() - this.startedTime;

        if (elapsed < 10000) {
            log.info("We're not started yet");
            return false;
        } else {
            log.info("We're started!");
            return true;
        }
    }

    @Override
    public String getStatus() {
        return "Working hard!";
    }
}
