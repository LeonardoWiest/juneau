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
package org.apache.juneau.urlencoding;

import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.urlencoding.annotation.*;

/**
 * Metadata on classes specific to the URL-Encoding serializers and parsers pulled from the {@link UrlEncoding @UrlEncoding} annotation on the class.
 */
public class UrlEncodingClassMeta extends ExtendedClassMeta {

	private final List<UrlEncoding> urlEncodings;
	private final boolean expandedParams;

	/**
	 * Constructor.
	 *
	 * @param cm The class that this annotation is defined on.
	 * @param mp URL-encoding metadata provider (for finding information about other artifacts).
	 */
	public UrlEncodingClassMeta(ClassMeta<?> cm, UrlEncodingMetaProvider mp) {
		super(cm);
		this.urlEncodings = cm.getAnnotations(UrlEncoding.class);

		boolean _expandedParams = false;
		for (UrlEncoding a : urlEncodings)
			if (a.expandedParams())
				_expandedParams = true;

		this.expandedParams = _expandedParams;
	}

	/**
	 * Returns the {@link UrlEncoding} annotations defined on the class.
	 *
	 * @return An unmodifiable list of annotations ordered parent-to-child, or an empty list if not found.
	 */
	protected List<UrlEncoding> getAnnotations() {
		return urlEncodings;
	}

	/**
	 * Returns the {@link UrlEncoding#expandedParams()} annotation defined on the class.
	 *
	 * @return The value of the {@link UrlEncoding#expandedParams()} annotation.
	 */
	protected boolean isExpandedParams() {
		return expandedParams;
	}
}
