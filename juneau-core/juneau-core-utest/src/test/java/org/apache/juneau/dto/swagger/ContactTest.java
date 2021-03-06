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
package org.apache.juneau.dto.swagger;

import static org.apache.juneau.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import java.net.*;

import org.apache.juneau.json.*;
import org.junit.*;

/**
 * Testcase for {@link Contact}.
 */
@FixMethodOrder(NAME_ASCENDING)
public class ContactTest {

	/**
	 * Test method for {@link Contact#name(java.lang.Object)}.
	 */
	@Test
	public void testName() {
		Contact t = new Contact();

		t.name("foo");
		assertEquals("foo", t.getName());

		t.name(new StringBuilder("foo"));
		assertEquals("foo", t.getName());
		assertObject(t.getName()).isType(String.class);

		t.name(null);
		assertNull(t.getName());
	}

	/**
	 * Test method for {@link Contact#url(java.lang.Object)}.
	 */
	@Test
	public void testUrl() {
		Contact t = new Contact();

		t.url("foo");
		assertEquals("foo", t.getUrl().toString());

		t.url(new StringBuilder("foo"));
		assertEquals("foo", t.getUrl().toString());
		assertObject(t.getUrl()).isType(URI.class);

		t.url(null);
		assertNull(t.getUrl());
	}

	/**
	 * Test method for {@link Contact#email(java.lang.Object)}.
	 */
	@Test
	public void testEmail() {
		Contact t = new Contact();

		t.email("foo");
		assertEquals("foo", t.getEmail());

		t.email(new StringBuilder("foo"));
		assertEquals("foo", t.getEmail());
		assertObject(t.getEmail()).isType(String.class);

		t.email(null);
		assertNull(t.getEmail());
	}

	/**
	 * Test method for {@link Contact#set(String, Object)}.
	 */
	@Test
	public void testSet() throws Exception {
		Contact t = new Contact();

		t
			.set("name", "foo")
			.set("url", "bar")
			.set("email", "baz")
			.set("$ref", "qux");

		assertObject(t).json().is("{name:'foo',url:'bar',email:'baz','$ref':'qux'}");

		t
			.set("name", new StringBuilder("foo"))
			.set("url", new StringBuilder("bar"))
			.set("email", new StringBuilder("baz"))
			.set("$ref", new StringBuilder("qux"));

		assertObject(t).json().is("{name:'foo',url:'bar',email:'baz','$ref':'qux'}");

		assertEquals("foo", t.get("name", String.class));
		assertEquals("bar", t.get("url", URI.class).toString());
		assertEquals("baz", t.get("email", String.class));
		assertEquals("qux", t.get("$ref", String.class));

		assertObject(t.get("name", String.class)).isType(String.class);
		assertObject(t.get("url", URI.class)).isType(URI.class);
		assertObject(t.get("email", String.class)).isType(String.class);
		assertObject(t.get("$ref", String.class)).isType(String.class);

		t.set("null", null).set(null, "null");
		assertNull(t.get("null", Object.class));
		assertNull(t.get(null, Object.class));
		assertNull(t.get("foo", Object.class));

		assertObject(JsonParser.DEFAULT.parse("{name:'foo',url:'bar',email:'baz','$ref':'qux'}", Contact.class)).json().is("{name:'foo',url:'bar',email:'baz','$ref':'qux'}");
	}
}
