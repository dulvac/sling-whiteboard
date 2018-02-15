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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.startupcheck.core.OsgiInstallerCheck;
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

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 *
 */

@Component(
        name = "Startup Check Monitor",
        service = {OsgiInstallerCheck.class, StartupCheck.class},
        configurationPid = "org.apache.sling.startupcheck.core.OsgiInstallerCheckImpl"
)
public class OsgiInstallerCheckImpl implements OsgiInstallerCheck {

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
     *
     */
    @Override
    public boolean isStarted() {


    }

    @Override
    public String getStatus() {
        return "I'm making sure all services are started at boot!";
    }

}
