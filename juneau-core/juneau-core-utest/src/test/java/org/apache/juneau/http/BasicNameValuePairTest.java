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
package org.apache.juneau.http;

import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class BasicNameValuePairTest {

	@Test
	public void a01_of_pairString() {
		BasicNameValuePair h = BasicNameValuePair.ofPair("Foo:bar");
		assertEquals("Foo", h.getName());
		assertEquals("bar", h.getValue());

		h = BasicNameValuePair.ofPair(" Foo : bar ");
		assertEquals("Foo", h.getName());
		assertEquals("bar", h.getValue());

		h = BasicNameValuePair.ofPair(" Foo : bar : baz ");
		assertEquals("Foo", h.getName());
		assertEquals("bar : baz", h.getValue());

		h = BasicNameValuePair.ofPair("Foo");
		assertEquals("Foo", h.getName());
		assertEquals("", h.getValue());

		assertNull(BasicNameValuePair.ofPair((String)null));
	}
}