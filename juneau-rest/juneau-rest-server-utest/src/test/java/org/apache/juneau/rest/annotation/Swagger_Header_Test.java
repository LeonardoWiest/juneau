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
package org.apache.juneau.rest.annotation;

import static org.apache.juneau.assertions.Assertions.*;
import static org.apache.juneau.rest.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import java.util.*;

import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.http.annotation.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class Swagger_Header_Test {

	//-----------------------------------------------------------------------------------------------------------------
	// Swagger tests
	//-----------------------------------------------------------------------------------------------------------------

	@Rest
	public static class A {

		@Header(
			name="H",
			description={"a","b"},
			type="string"
		)
		public static class A1 {
			public A1(String x) {}
		}
		@RestMethod
		public void a(A1 h) {}

		@Header(
			name="H",
			api={
				"description:'a\nb',",
				"type:'string'"
			}
		)
		public static class A2 {
			public A2(String x) {}
		}
		@RestMethod
		public void b(A2 h) {}

		@Header(
			name="H",
			api={
				"description:'b\nc',",
				"type:'string'"
			},
			description={"a","b"},
			type="string"
		)
		public static class A3 {
			public A3(String x) {}
		}
		@RestMethod
		public void c(A3 h) {}
	}

	@Test
	public void a01_fromPojo() throws Exception {
		Swagger s = getSwagger(A.class);
		ParameterInfo x;

		x = s.getParameterInfo("/a","get","header","H");
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getType()).json().is("'string'");

		x = s.getParameterInfo("/b","get","header","H");
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getType()).json().is("'string'");

		x = s.getParameterInfo("/c","get","header","H");
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getType()).json().is("'string'");
	}

	@Rest
	public static class B {

		@Header(name="H")
		public static class B1 {}
		@RestMethod
		public void a(B1 h) {}

		@Header(name="H")
		public static class B2 {
			public String f1;
		}
		@RestMethod
		public void b(B2 b) {}

		@Header(name="H")
		public static class B3 extends LinkedList<String> {
			private static final long serialVersionUID = 1L;
		}
		@RestMethod
		public void c(B3 b) {}

		@Header(name="H")
		public static class B4 {}
		@RestMethod
		public void d(B4 b) {}
	}

	@Test
	public void b01_schemaFromPojo() throws Exception {
		Swagger s = getSwagger(B.class);
		ParameterInfo x;

		x = s.getParameterInfo("/a","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'string'}");

		x = s.getParameterInfo("/b","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'object',schema:{properties:{f1:{type:'string'}}}}");

		x = s.getParameterInfo("/c","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'array',items:{type:'string'}}");

		x = s.getParameterInfo("/d","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'string'}");
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Examples
	//-----------------------------------------------------------------------------------------------------------------

	@Rest
	public static class C {

		@Header(name="H", example="{f1:'a'}")
		public static class C1 {
			public String f1;
		}
		@RestMethod
		public void a(C1 h) {}
	}

	@Test
	public void c01_exampleFromPojo() throws Exception {
		Swagger s = getSwagger(C.class);

		ParameterInfo x = s.getParameterInfo("/a","get","header","H");
		assertEquals("{f1:'a'}", x.getExample());
	}

	@Rest
	public static class D {

		@RestMethod
		public void a(
			@Header(
				name="H",
				description={"a","b"},
				type="string"
			) String h) {}

		@RestMethod
		public void b(
			@Header(
				name="H",
				api={
					"description:'a\nb',",
					"type:'string'",
				}
			) String h) {}

		@RestMethod
		public void c(
			@Header(
				name="H",
				api={
					"description:'b\nc',",
					"type:'string'",
				},
				description={"a","b"},
				type="string"
			) String h) {}

		@RestMethod
		public void d(@Header("H") String h) {}
	}

	@Test
	public void d01_fromParameter() throws Exception {
		Swagger s = getSwagger(D.class);
		ParameterInfo x;

		x = s.getParameterInfo("/a","get","header","H");
		assertEquals("H", x.getName());
		assertEquals("a\nb", x.getDescription());
		assertEquals("string", x.getType());

		x = s.getParameterInfo("/b","get","header","H");
		assertEquals("H", x.getName());
		assertEquals("a\nb", x.getDescription());
		assertEquals("string", x.getType());

		x = s.getParameterInfo("/c","get","header","H");
		assertEquals("H", x.getName());
		assertEquals("a\nb", x.getDescription());
		assertEquals("string", x.getType());

		x = s.getParameterInfo("/d","get","header","H");
		assertEquals("H", x.getName());
	}

	@Rest
	public static class E {

		@RestMethod
		public void a(@Header(name="H") String h) {}

		public static class E2 {
			public String f1;
		}
		@RestMethod
		public void b(@Header("H") E2 b) {}

		public static class E3 extends LinkedList<String> {
			private static final long serialVersionUID = 1L;
		}
		@RestMethod
		public void c(@Header("H") E3 b) {}

		public static class E4 {}
		@RestMethod
		public void d(@Header("H") E4 b) {}

		@RestMethod
		public void e(@Header("H") Integer b) {}

		@RestMethod
		public void f(@Header("H") Boolean b) {}
	}

	@Test
	public void e01_schemaFromParameter() throws Exception {
		Swagger s = getSwagger(E.class);
		ParameterInfo x;

		x = s.getParameterInfo("/a","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'string'}");

		x = s.getParameterInfo("/b","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'object',schema:{properties:{f1:{type:'string'}}}}");

		x = s.getParameterInfo("/c","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'array',items:{type:'string'}}");

		x = s.getParameterInfo("/d","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'string'}");

		x = s.getParameterInfo("/e","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'integer',format:'int32'}");

		x = s.getParameterInfo("/f","get","header","H");
		assertObject(x).json().is("{'in':'header',name:'H',type:'boolean'}");
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Examples
	//-----------------------------------------------------------------------------------------------------------------

	@Rest
	public static class F {

		@RestMethod
		public void a(@Header(name="H", example={"a","b"}) String h) {}
	}

	@Test
	public void f01_exampleFromParameter() throws Exception {
		Swagger s = getSwagger(F.class);

		ParameterInfo x = s.getParameterInfo("/a","get","header","H");
		assertEquals("a\nb", x.getExample());
	}
}
