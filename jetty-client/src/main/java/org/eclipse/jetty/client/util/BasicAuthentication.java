//
//  ========================================================================
//  Copyright (c) 1995-2012 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.client.util;

import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.util.StringUtil;

public class BasicAuthentication implements Authentication
{
    private final String uri;
    private final String realm;
    private final String user;
    private final String password;

    public BasicAuthentication(String uri, String realm, String user, String password)
    {
        this.uri = uri;
        this.realm = realm;
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean matches(String type, String uri, String realm)
    {
        if (!"basic".equalsIgnoreCase(type))
            return false;

        if (!uri.startsWith(this.uri))
            return false;

        return this.realm.equals(realm);
    }

    @Override
    public Result authenticate(Request request, ContentResponse response, String wwwAuthenticate, Attributes context)
    {
        String encoding = StringUtil.__ISO_8859_1;
        String value = "Basic " + B64Code.encode(user + ":" + password, encoding);
        return new BasicResult(request.getURI(), value);
    }

    private static class BasicResult implements Result
    {
        private final String uri;
        private final String value;

        public BasicResult(String uri, String value)
        {
            this.uri = uri;
            this.value = value;
        }

        @Override
        public String getURI()
        {
            return uri;
        }

        @Override
        public void apply(Request request)
        {
            if (request.getURI().startsWith(uri))
                request.header(HttpHeader.AUTHORIZATION.asString(), value);
        }

        @Override
        public String toString()
        {
            return String.format("Basic authentication result for %s", uri);
        }
    }
}
