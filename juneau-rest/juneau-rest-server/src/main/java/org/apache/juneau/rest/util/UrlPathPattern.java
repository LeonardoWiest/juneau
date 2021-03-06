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
package org.apache.juneau.rest.util;

import static org.apache.juneau.internal.StringUtils.*;

import java.util.*;
import java.util.regex.*;

import org.apache.juneau.annotation.*;
import org.apache.juneau.rest.annotation.*;

/**
 * A parsed path pattern constructed from a {@link RestMethod#path() @RestMethod(path)} value.
 *
 * <p>
 * Handles aspects of matching and precedence ordering.
 */
@BeanIgnore
public final class UrlPathPattern implements Comparable<UrlPathPattern> {

	private static final Pattern VAR_PATTERN = Pattern.compile("\\{([^\\}]+)\\}");

	private final String pattern, comparator;
	private final String[] parts, vars, varKeys;
	private final boolean hasRemainder;

	/**
	 * Constructor.
	 *
	 * @param patternString The raw pattern string from the {@link RestMethod#path() @RestMethod(path)} annotation.
	 */
	public UrlPathPattern(String patternString) {
		this.pattern = isEmpty(patternString) ? "/" : patternString.charAt(0) != '/' ? '/' + patternString : patternString;

		String c = patternString.replaceAll("\\{[^\\}]+\\}", ".").replaceAll("\\w+", "X").replaceAll("\\.", "W");
		if (c.isEmpty())
			c = "+";
		if (! c.endsWith("/*"))
			c = c + "/W";
		this.comparator = c;

		String[] parts = new UrlPathInfo(pattern).getParts();

		this.hasRemainder = parts.length > 0 && "*".equals(parts[parts.length-1]);

		parts = hasRemainder ? Arrays.copyOf(parts, parts.length-1) : parts;

		this.parts = parts;
		this.vars = new String[parts.length];
		List<String> vars = new ArrayList<>();

		for (int i = 0; i < parts.length; i++) {
			Matcher m = VAR_PATTERN.matcher(parts[i]);
			if (m.matches()) {
				this.vars[i] = m.group(1);
				vars.add(this.vars[i]);
			}
		}

		this.varKeys = vars.isEmpty() ? null : vars.toArray(new String[vars.size()]);
	}

	/**
	 * Returns a non-<jk>null</jk> value if the specified path matches this pattern.
	 *
	 * @param path The path to match against.
	 * @return
	 * 	A pattern match object, or <jk>null</jk> if the path didn't match this pattern.
	 */
	public UrlPathPatternMatch match(String path) {
		return match(new UrlPathInfo(path));
	}

	/**
	 * Returns a non-<jk>null</jk> value if the specified path matches this pattern.
	 *
	 * @param pathInfo The path to match against.
	 * @return
	 * 	A pattern match object, or <jk>null</jk> if the path didn't match this pattern.
	 */
	public UrlPathPatternMatch match(UrlPathInfo pathInfo) {

		String[] pip = pathInfo.getParts();

		if (parts.length != pip.length) {
			if (hasRemainder) {
				if (pip.length == parts.length - 1 && ! pathInfo.isTrailingSlash())
					return null;
				else if (pip.length < parts.length)
					return null;
			} else {
				if (pip.length != parts.length + 1)
					return null;
				if (! pathInfo.isTrailingSlash())
					return null;
			}
		}

		for (int i = 0; i < parts.length; i++)
			if (vars[i] == null && (pip.length <= i || ! ("*".equals(parts[i]) || pip[i].equals(parts[i]))))
				return null;

		String[] vals = varKeys == null ? null : new String[varKeys.length];

		int j = 0;
		if (vals != null)
			for (int i = 0; i < parts.length; i++)
				if (vars[i] != null)
					vals[j++] = pip[i];

		return new UrlPathPatternMatch(pathInfo.getPath(), parts.length, varKeys, vals);
	}

	/**
	 * Returns the variable names found in the pattern.
	 *
	 * @return
	 * 	The variable names or an empty array if no variables found.
	 *	<br>Modifying the returned array does not modify this object.
	 */
	public String[] getVars() {
		return varKeys == null ? new String[0] : Arrays.copyOf(varKeys, varKeys.length);
	}

	/**
	 * Returns <jk>true</jk> if this path pattern contains variables.
	 *
	 * @return <jk>true</jk> if this path pattern contains variables.
	 */
	public boolean hasVars() {
		return varKeys != null;
	}

	/**
	 * Comparator for this object.
	 *
	 * <p>
	 * The comparator is designed to order URL pattern from most-specific to least-specific.
	 * For example, the following patterns would be ordered as follows:
	 * <ol>
	 * 	<li><c>/foo/bar</c>
	 * 	<li><c>/foo/bar/*</c>
	 * 	<li><c>/foo/{id}/bar</c>
	 * 	<li><c>/foo/{id}/bar/*</c>
	 * 	<li><c>/foo/{id}</c>
	 * 	<li><c>/foo/{id}/*</c>
	 * 	<li><c>/foo</c>
	 * 	<li><c>/foo/*</c>
	 * </ol>
	 */
	@Override /* Comparable */
	public int compareTo(UrlPathPattern o) {
		return o.comparator.compareTo(comparator);
	}

	@Override /* Object */
	public String toString() {
		return pattern.toString();
	}
}