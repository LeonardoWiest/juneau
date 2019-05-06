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

import static org.junit.Assert.*;

import java.util.function.*;

import org.apache.juneau.html.annotation.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.utils.*;
import org.junit.*;

/**
 * Tests the @HtmlConfig annotation.
 */
public class HtmlConfigAnnotationTest {

	private static void check(String expected, Object o) {
		assertEquals(expected, TO_STRING.apply(o));
	}

	private static final Function<Object,String> TO_STRING = new Function<Object,String>() {
		@Override
		public String apply(Object t) {
			if (t == null)
				return null;
			return t.toString();
		}
	};

	static StringResolver sr = new StringResolver() {
		@Override
		public String resolve(String input) {
			if (input.startsWith("$"))
				input = input.substring(1);
			return input;
		}
	};

	//-----------------------------------------------------------------------------------------------------------------
	// Basic tests
	//-----------------------------------------------------------------------------------------------------------------

	@HtmlConfig(
		addBeanTypes="$true",
		addKeyValueTableHeaders="$true",
		detectLabelParameters="$true",
		detectLinksInStrings="$true",
		labelParameter="$foo",
		uriAnchorText="$TO_STRING"
	)
	static class A {}
	static ClassInfo a = ClassInfo.of(A.class);

	@Test
	public void basicSerializer() throws Exception {
		AnnotationsMap m = a.getAnnotationsMap();
		HtmlSerializerSession x = HtmlSerializer.create().applyAnnotations(m, sr).build().createSession();
		check("true", x.isAddBeanTypes());
		check("true", x.isAddKeyValueTableHeaders());
		check("true", x.isDetectLabelParameters());
		check("true", x.isDetectLinksInStrings());
		check("foo", x.getLabelParameter());
		check("TO_STRING", x.getUriAnchorText());
	}

	@Test
	public void basicParser() throws Exception {
		AnnotationsMap m = a.getAnnotationsMap();
		HtmlParser.create().applyAnnotations(m, sr).build().createSession();
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Annotation with no values.
	//-----------------------------------------------------------------------------------------------------------------

	@HtmlConfig()
	static class B {}
	static ClassInfo b = ClassInfo.of(B.class);

	@Test
	public void defaultsSerializer() throws Exception {
		AnnotationsMap m = b.getAnnotationsMap();
		HtmlSerializerSession x = HtmlSerializer.create().applyAnnotations(m, sr).build().createSession();
		check("false", x.isAddBeanTypes());
		check("false", x.isAddKeyValueTableHeaders());
		check("true", x.isDetectLabelParameters());
		check("true", x.isDetectLinksInStrings());
		check("label", x.getLabelParameter());
		check("TO_STRING", x.getUriAnchorText());
	}

	@Test
	public void defaultsParser() throws Exception {
		AnnotationsMap m = b.getAnnotationsMap();
		HtmlParser.create().applyAnnotations(m, sr).build().createSession();
	}

	//-----------------------------------------------------------------------------------------------------------------
	// No annotation.
	//-----------------------------------------------------------------------------------------------------------------

	static class C {}
	static ClassInfo c = ClassInfo.of(C.class);

	@Test
	public void noAnnotationSerializer() throws Exception {
		AnnotationsMap m = c.getAnnotationsMap();
		HtmlSerializerSession x = HtmlSerializer.create().applyAnnotations(m, sr).build().createSession();
		check("false", x.isAddBeanTypes());
		check("false", x.isAddKeyValueTableHeaders());
		check("true", x.isDetectLabelParameters());
		check("true", x.isDetectLinksInStrings());
		check("label", x.getLabelParameter());
		check("TO_STRING", x.getUriAnchorText());
	}

	@Test
	public void noAnnotationParser() throws Exception {
		AnnotationsMap m = c.getAnnotationsMap();
		HtmlParser.create().applyAnnotations(m, sr).build().createSession();
	}
}