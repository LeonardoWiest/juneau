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

import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import java.util.*;

import org.apache.juneau.collections.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.json.*;
import org.apache.juneau.rest.client.*;
import org.apache.juneau.rest.mock.*;
import org.apache.juneau.testutils.pojos.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class Header_Test {

	//------------------------------------------------------------------------------------------------------------------
	// Optional header parameter.
	//------------------------------------------------------------------------------------------------------------------

	@Rest(serializers=SimpleJsonSerializer.class)
	public static class A {
		@RestMethod
		public Object a(@Header("f1") Optional<Integer> f1) throws Exception {
			assertNotNull(f1);
			return f1;
		}
		@RestMethod
		public Object b(@Header("f1") Optional<ABean> f1) throws Exception {
			assertNotNull(f1);
			return f1;
		}
		@RestMethod
		public Object c(@Header("f1") Optional<List<ABean>> f1) throws Exception {
			assertNotNull(f1);
			return f1;
		}
		@RestMethod
		public Object d(@Header("f1") List<Optional<ABean>> f1) throws Exception {
			return f1;
		}
	}

	@Test
	public void a01_optionalParams() throws Exception {
		RestClient a = MockRestClient.buildJson(A.class);

		a.get("/a").header("f1", 123)
			.run()
			.assertCode().is(200)
			.assertBody().is("123");
		a.get("/a")
			.run()
			.assertCode().is(200)
			.assertBody().is("null");

		a.get("/b")
			.header("f1", "a=1,b=foo")
			.run()
			.assertCode().is(200)
			.assertBody().is("{a:1,b:'foo'}");
		a.get("/b")
			.run()
			.assertCode().is(200)
			.assertBody().is("null");

		a.get("/c")
			.header("f1", "@((a=1,b=foo))")
			.run()
			.assertCode().is(200)
			.assertBody().is("[{a:1,b:'foo'}]");
		a.get("/c")
			.run()
			.assertCode().is(200)
			.assertBody().is("null");

		a.get("/d")
			.header("f1", "@((a=1,b=foo))")
			.run()
			.assertCode().is(200)
			.assertBody().is("[{a:1,b:'foo'}]");
		a.get("/d")
			.run()
			.assertCode().is(200)
			.assertBody().is("null");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Default values - Annotated headers.
	//------------------------------------------------------------------------------------------------------------------

	@Rest
	public static class B {
		@RestMethod
		public OMap a(@Header(name="H1") String h1, @Header("H2") String h2, @Header("H3") String h3) {
			return OMap.of()
				.a("h1", h1)
				.a("h2", h2)
				.a("h3", h3);
		}
	}

	@Test
	public void b01_annotatedHeaders() throws Exception {
		RestClient b = MockRestClient.build(B.class);
		b.get("/a").run().assertBody().is("{h1:null,h2:null,h3:null}");
		b.get("/a").header("H1",4).header("H2",5).header("H3",6).run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
		b.get("/a").header("h1",4).header("h2",5).header("h3",6).run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Default values - Annotated headers, case-insensitive matching.
	//------------------------------------------------------------------------------------------------------------------

	@Rest
	public static class C {
		@RestMethod
		public OMap a(@Header("h1") String h1, @Header("h2") String h2, @Header("h3") String h3) {
			return OMap.of()
				.a("h1", h1)
				.a("h2", h2)
				.a("h3", h3);
		}
	}

	@Test
	public void c01_annotatedHeadersCaseInsensitive() throws Exception {
		RestClient c = MockRestClient.build(C.class);
		c.get("/a").run().assertBody().is("{h1:null,h2:null,h3:null}");
		c.get("/a").header("H1",4).header("H2",5).header("H3",6).run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
		c.get("/a").header("h1",4).header("h2",5).header("h3",6).run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Default values - Annotated headers with default values.
	//------------------------------------------------------------------------------------------------------------------

	@Rest
	public static class D1 {
		@RestMethod
		public OMap a(@Header(name="h1",_default="1") String h1, @Header(name="h2",_default="2") String h2, @Header(name="h3",_default="3") String h3) {
			return OMap.of()
				.a("h1", h1)
				.a("h2", h2)
				.a("h3", h3);
		}
	}

	@Test
	public void d01_annotatedHeadersDefault() throws Exception {
		RestClient d1 = MockRestClient.build(D1.class);
		d1.get("/a").run().assertBody().is("{h1:'1',h2:'2',h3:'3'}");
		d1.get("/a").header("H1",4).header("H2",5).header("H3",6).run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
		d1.get("/a").header("h1",4).header("h2",5).header("h3",6).run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
	}

	@Rest
	public static class D2 {
		@RestMethod
		public OMap a(@Header(value="h1",_default="1") String h1, @Header(value="h2",_default="2") String h2, @Header(value="h3",_default="3") String h3) {
			return OMap.of()
				.a("h1", h1)
				.a("h2", h2)
				.a("h3", h3);
		}
	}

	@Test
	public void d02_annotatedHeadersDefault() throws Exception {
		RestClient d2 = MockRestClient.build(D2.class);
		d2.get("/a").run().assertBody().is("{h1:'1',h2:'2',h3:'3'}");
		d2.get("/a").header("H1",4).header("H2",5).header("H3",6).run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
		d2.get("/a").header("h1",4).header("h2",5).header("h3",6).run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Default values - Annotated headers with default values and default request headers.
	//------------------------------------------------------------------------------------------------------------------

	@Rest
	public static class E {
		@RestMethod(reqHeaders={"H1:1","H2=2"," H3 : 3 "})
		public OMap a(@Header(value="h1",_default="4") String h1, @Header(value="h2",_default="5") String h2, @Header(value="h3",_default="6") String h3) {
			return OMap.of()
				.a("h1", h1)
				.a("h2", h2)
				.a("h3", h3);
		}
	}

	@Test
	public void e01_annotatedAndDefaultHeaders() throws Exception {
		RestClient e = MockRestClient.build(E.class);
		e.get("/a").run().assertBody().is("{h1:'4',h2:'5',h3:'6'}");
		e.get("/a").header("H1",7).header("H2",8).header("H3",9).run().assertBody().is("{h1:'7',h2:'8',h3:'9'}");
		e.get("/a").header("h1",7).header("h2",8).header("h3",9).run().assertBody().is("{h1:'7',h2:'8',h3:'9'}");
	}
}
