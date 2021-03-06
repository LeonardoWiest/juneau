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
package org.apache.juneau.json;

import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.json.annotation.*;

/**
 * Metadata on classes specific to the JSON serializers and parsers pulled from the {@link Json @Json} annotation on
 * the class.
 */
public class JsonClassMeta extends ExtendedClassMeta {

	private final List<Json> jsons;
	private final String wrapperAttr;

	/**
	 * Constructor.
	 *
	 * @param cm The class that this annotation is defined on.
	 * @param mp JSON metadata provider (for finding information about other artifacts).
	 */
	public JsonClassMeta(ClassMeta<?> cm, JsonMetaProvider mp) {
		super(cm);
		this.jsons = cm.getAnnotations(Json.class);
		
		String _wrapperAttr = null;
		for (Json a : this.jsons)
			if (! a.wrapperAttr().isEmpty())
				_wrapperAttr = a.wrapperAttr();
		this.wrapperAttr = _wrapperAttr;
	}

	/**
	 * Returns the {@link Json @Json} annotations defined on the class.
	 *
	 * @return An unmodifiable list of annotations ordered parent-to-child, or an empty list if not found.
	 */
	protected List<Json> getAnnotations() {
		return jsons;
	}

	/**
	 * Returns the {@link Json#wrapperAttr() @Json(wrapperAttr)} annotation defined on the class.
	 *
	 * @return The value of the annotation, or <jk>null</jk> if not specified.
	 */
	protected String getWrapperAttr() {
		return wrapperAttr;
	}
}
