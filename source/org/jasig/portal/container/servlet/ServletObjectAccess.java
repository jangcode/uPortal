/**
 * Copyright � 2004 The JA-SIG Collaborative.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the JA-SIG Collaborative
 *    (http://www.jasig.org/)."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JA-SIG COLLABORATIVE "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JA-SIG COLLABORATIVE OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.jasig.portal.container.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.factory.FactoryManager;

public class ServletObjectAccess {

//    public static HttpServletRequest getServletRequest(HttpServletRequest request, PortletWindow portletWindow) {
//        return getRequestFactory().getServletRequest(request, portletWindow);
//    }

    public static HttpServletResponse getServletResponse(HttpServletResponse response) {
        return getResponseFactory().getServletResponse(response);
    }

    public static HttpServletResponse getStoredServletResponse(HttpServletResponse response, PrintWriter writer) {
        return getResponseFactory().getStoredServletResponse(response, writer);
    }

//    private static ServletRequestFactory getRequestFactory() {
//        return (ServletRequestFactory)FactoryManager.getFactory(javax.servlet.http.HttpServletRequest.class);
//    }
    
    private static ServletResponseFactory getResponseFactory() {
        return (ServletResponseFactory)FactoryManager.getFactory(javax.servlet.http.HttpServletResponse.class);
    }
}
