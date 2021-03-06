// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.html;

import java.util.*;

import org.apache.juneau.html.annotation.*;
import org.apache.juneau.svl.*;

/**
 * HTML widget variable resolver.
 *
 * <p>
 * The format for this var is <js>"$W{widgetName}"</js>.
 *
 * <p>
 * Widgets are simple class that produce some sort of string based on a passed-in HTTP request.
 *
 * <p>
 * They're registered via the following mechanisms:
 * <ul>
 * 	<li>{@link HtmlDocConfig#widgets() @HtmlDocConfig(widgets)}
 * </ul>
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc RestSvlVariables}
 * </ul>
 */
public class HtmlWidgetVar extends SimpleVar {

	/**
	 * The name of the session or context object that identifies the object containing the widgets to resolve.
	 */
	public static final String SESSION_htmlWidgets = "htmlWidgets";

	/**
	 * The name of this variable.
	 */
	public static final String NAME = "W";

	/**
	 * Constructor.
	 */
	public HtmlWidgetVar() {
		super(NAME);
	}

	@SuppressWarnings("unchecked")
	@Override /* Parameter */
	public String resolve(VarResolverSession session, String key) throws Exception {
		Map<String,HtmlWidget> widgets = (Map<String,HtmlWidget>)session.getSessionObject(Object.class, SESSION_htmlWidgets, false);

		HtmlWidget w = widgets.get(key);
		if (w == null)
			return "unknown-widget-"+key;

		return w.getHtml(session);
	}

	@Override
	public boolean canResolve(VarResolverSession session) {
		return session.hasSessionObject(SESSION_htmlWidgets);
	}
}