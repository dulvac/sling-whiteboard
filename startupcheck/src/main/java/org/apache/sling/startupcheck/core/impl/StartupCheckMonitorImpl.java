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
package org.apache.sling.startupcheck.core.impl;

import java.io.IOException;
import java.util.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.startupcheck.core.StartupCheck;
import org.apache.sling.startupcheck.core.monitor.StartupCheckMonitor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

/**
 *
 */

@Component(
        name = "Startup Check Monitor",
        service = {StartupCheckMonitor.class, Servlet.class},
        configurationPid = "org.apache.sling.startupcheck.core.StartupCheckMonitorImpl",
        property = {
                SLING_SERVLET_PATHS + "=/system/startupcheck",
                SLING_SERVLET_METHODS + "=GET"
        }
)
public class StartupCheckMonitorImpl extends SlingSafeMethodsServlet implements StartupCheckMonitor {

    public static final String INSTANCE_READY = "Instance ready";
    public static final String INSTANCE_NOT_READY = "Instance not ready";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private BundleContext bundleContext;
    private ServiceTracker startupCheckTracker;


    @Activate
    protected void activate(final BundleContext ctx, final Map<String, Object> properties) throws InterruptedException {
        this.bundleContext = ctx;

        this.startupCheckTracker = new ServiceTracker(bundleContext, StartupCheck.class, null);
        this.startupCheckTracker.open();
        log.info("Activated");
    }

    @Deactivate
    protected void deactivate() throws InterruptedException {
        this.startupCheckTracker.close();
        this.startupCheckTracker = null;
        bundleContext = null;
    }

    /**
     * Process polling requests
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        if (!this.isStarted()) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, INSTANCE_NOT_READY);
            return;
        } else {
            response.getWriter().print(INSTANCE_READY); // all servicechecks reported that they started.
            response.flushBuffer();
        }
    }

    /**
     *
     */
    @Override
    public boolean isStarted() {

        // Get reference to all {{StartupCheck}} services
        final ServiceReference<StartupCheck>[] startupChecks = this.startupCheckTracker.getServiceReferences();
        if (null == startupChecks) {
            return true;
        }

        boolean allStarted = true;
        for (ServiceReference<StartupCheck> ref : startupChecks) {
            StartupCheck sc = this.bundleContext.getService(ref);
            if (!sc.isStarted()) {
                log.info("Found check that reported NOT STARTED: {} [{}]", sc.isStarted(), sc);
                allStarted = false;
                break;
            }
        }
        return allStarted;
    }

}
