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
package org.apache.juneau.rest.test.client;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.apache.juneau.assertions.Assertions.*;
import static org.apache.juneau.http.HttpMethod.*;
import static org.apache.juneau.testutils.Constants.*;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.apache.juneau.collections.*;
import org.apache.juneau.http.annotation.Body;
import org.apache.juneau.http.annotation.FormData;
import org.apache.juneau.http.annotation.Header;
import org.apache.juneau.http.annotation.Path;
import org.apache.juneau.http.annotation.Query;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.serializer.annotation.*;
import org.apache.juneau.testutils.pojos.*;

/**
 * JUnit automated testcase resource.
 */
@Rest(
	path="/testThirdPartyProxy",
	logging=@Logging(
		disabled="true"
	)
)
@SerializerConfig(addRootType="true",addBeanTypes="true")
@SuppressWarnings({"serial"})
public class ThirdPartyProxyResource extends BasicRestServletJena {

	public static FileWriter logFile;
	static {
		try {
			logFile = new FileWriter("./target/logs/third-party-proxy-resource.txt", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RestHook(HookEvent.START_CALL)
	public static void startCall(HttpServletRequest req) {
		try {
			logFile.append("START["+new Date()+"]-").append(req.getQueryString()).append("\n");
			logFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RestHook(HookEvent.PRE_CALL)
	public static void preCall(HttpServletRequest req) {
		try {
			logFile.append("PRE["+new Date()+"]-").append(req.getQueryString()).append("\n");
			logFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RestHook(HookEvent.POST_CALL)
	public static void postCall(HttpServletRequest req) {
		try {
			logFile.append("POST["+new Date()+"]-").append(req.getQueryString()).append("\n");
			logFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RestHook(HookEvent.END_CALL)
	public static void endCall(HttpServletRequest req) {
		try {
			Throwable e = (Throwable)req.getAttribute("Exception");
			Long execTime = (Long)req.getAttribute("ExecTime");
			logFile.append("END["+new Date()+"]-").append(req.getQueryString()).append(", time=").append(""+execTime).append(", exception=").append(e == null ? null : e.toString()).append("\n");
			logFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Header tests
	//-----------------------------------------------------------------------------------------------------------------

	@RestMethod(method=GET, path="/primitiveHeaders")
	public String primitiveHeaders(
			@Header("a") String a,
			@Header("an") String an,
			@Header("b") int b,
			@Header("c") Integer c,
			@Header("cn") Integer cn,
			@Header("d") Boolean d,
			@Header("e") float e,
			@Header("f") Float f
		) throws Exception {

		assertEquals(a, "foo");
		assertNull(an);
		assertEquals(123, b);
		assertEquals(123, (int)c);
		assertNull(cn);
		assertTrue(d);
		assertTrue(1f == e);
		assertTrue(1f == f);
		return "OK";
	}

	@RestMethod(method=GET, path="/primitiveCollectionHeaders")
	public String primitiveCollectionHeaders(
			@Header("a") int[][][] a,
			@Header("b") Integer[][][] b,
			@Header("c") String[][][] c,
			@Header("d") List<Integer> d,
			@Header("e") List<List<List<Integer>>> e,
			@Header("f") List<Integer[][][]> f,
			@Header("g") List<int[][][]> g,
			@Header("h") List<String> h
		) throws Exception {

		assertObject(a).json().is("[[[1,2],null],null]");
		assertObject(b).json().is("[[[1,null],null],null]");
		assertObject(c).json().is("[[['foo',null],null],null]");
		assertObject(d).json().is("[1,null]");
		assertObject(e).json().is("[[[1,null],null],null]");
		assertObject(f).json().is("[[[[1,null],null],null],null]");
		assertObject(g).json().is("[[[[1,2],null],null],null]");
		assertObject(h).json().is("['foo','bar',null]");

		assertObject(d.get(0)).isType(Integer.class);
		assertObject(e.get(0).get(0).get(0)).isType(Integer.class);
		assertObject(f.get(0)).isType(Integer[][][].class);
		assertObject(g.get(0)).isType(int[][][].class);

		return "OK";
	}

	@RestMethod(method=GET, path="/beanHeaders")
	public String beanHeaders(
			@Header(name="a",cf="uon") ABean a,
			@Header(name="an",cf="uon") ABean an,
			@Header(name="b",cf="uon") ABean[][][] b,
			@Header(name="c",cf="uon") List<ABean> c,
			@Header(name="d",cf="uon") List<ABean[][][]> d,
			@Header(name="e",cf="uon") Map<String,ABean> e,
			@Header(name="f",cf="uon") Map<String,List<ABean>> f,
			@Header(name="g",cf="uon") Map<String,List<ABean[][][]>> g,
			@Header(name="h",cf="uon") Map<Integer,List<ABean>> h
		) throws Exception {

		assertObject(a).json().is("{a:1,b:'foo'}");
		assertNull(an);
		assertObject(b).json().is("[[[{a:1,b:'foo'},null],null],null]");
		assertObject(c).json().is("[{a:1,b:'foo'},null]");
		assertObject(d).json().is("[[[[{a:1,b:'foo'},null],null],null],null]");
		assertObject(e).json().is("{foo:{a:1,b:'foo'}}");
		assertObject(f).json().is("{foo:[{a:1,b:'foo'}]}");
		assertObject(g).json().is("{foo:[[[[{a:1,b:'foo'},null],null],null],null]}");
		assertObject(h).json().is("{'1':[{a:1,b:'foo'}]}");

		assertObject(c.get(0)).isType(ABean.class);
		assertObject(d.get(0)).isType(ABean[][][].class);
		assertObject(e.get("foo")).isType(ABean.class);
		assertObject(f.get("foo").get(0)).isType(ABean.class);
		assertObject(g.get("foo").get(0)).isType(ABean[][][].class);
		assertObject(h.keySet().iterator().next()).isType(Integer.class);
		assertObject(h.values().iterator().next().get(0)).isType(ABean.class);
		return "OK";
	}

	@RestMethod(method=GET, path="/typedBeanHeaders")
	public String typedBeanHeaders(
			@Header(n="a",cf="uon") TypedBean a,
			@Header(n="an",cf="uon") TypedBean an,
			@Header(n="b",cf="uon") TypedBean[][][] b,
			@Header(n="c",cf="uon") List<TypedBean> c,
			@Header(n="d",cf="uon") List<TypedBean[][][]> d,
			@Header(n="e",cf="uon") Map<String,TypedBean> e,
			@Header(n="f",cf="uon") Map<String,List<TypedBean>> f,
			@Header(n="g",cf="uon") Map<String,List<TypedBean[][][]>> g,
			@Header(n="h",cf="uon") Map<Integer,List<TypedBean>> h
		) throws Exception {

		assertObject(a).json().is("{a:1,b:'foo'}");
		assertNull(an);
		assertObject(b).json().is("[[[{a:1,b:'foo'},null],null],null]");
		assertObject(c).json().is("[{a:1,b:'foo'},null]");
		assertObject(d).json().is("[[[[{a:1,b:'foo'},null],null],null],null]");
		assertObject(e).json().is("{foo:{a:1,b:'foo'}}");
		assertObject(f).json().is("{foo:[{a:1,b:'foo'}]}");
		assertObject(g).json().is("{foo:[[[[{a:1,b:'foo'},null],null],null],null]}");
		assertObject(h).json().is("{'1':[{a:1,b:'foo'}]}");

		assertObject(a).isType(TypedBeanImpl.class);
		assertObject(b[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(c.get(0)).isType(TypedBeanImpl.class);
		assertObject(d.get(0)[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(e.get("foo")).isType(TypedBeanImpl.class);
		assertObject(f.get("foo").get(0)).isType(TypedBeanImpl.class);
		assertObject(g.get("foo").get(0)[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(h.keySet().iterator().next()).isType(Integer.class);
		assertObject(h.get(1).get(0)).isType(TypedBeanImpl.class);

		return "OK";
	}

	@RestMethod(method=GET, path="/swappedPojoHeaders")
	public String swappedPojoHeaders(
			@Header(n="a",cf="uon") SwappedPojo a,
			@Header(n="b",cf="uon") SwappedPojo[][][] b,
			@Header(n="c",cf="uon") Map<SwappedPojo,SwappedPojo> c,
			@Header(n="d",cf="uon") Map<SwappedPojo,SwappedPojo[][][]> d
		) throws Exception {

		assertObject(a).json().is("'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'");
		assertObject(b).json().is("[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]");
		assertObject(c).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'}");
		assertObject(d).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]}");

		assertObject(a).isType(SwappedPojo.class);
		assertObject(b[0][0][0]).isType(SwappedPojo.class);
		assertObject(c.keySet().iterator().next()).isType(SwappedPojo.class);
		assertObject(c.values().iterator().next()).isType(SwappedPojo.class);
		assertObject(d.keySet().iterator().next()).isType(SwappedPojo.class);
		assertObject(d.values().iterator().next()[0][0][0]).isType(SwappedPojo.class);

		return "OK";
	}

	@RestMethod(method=GET, path="/implicitSwappedPojoHeaders")
	public String implicitSwappedPojoHeaders(
			@Header(n="a",cf="uon") ImplicitSwappedPojo a,
			@Header(n="b",cf="uon") ImplicitSwappedPojo[][][] b,
			@Header(n="c",cf="uon") Map<ImplicitSwappedPojo,ImplicitSwappedPojo> c,
			@Header(n="d",cf="uon") Map<ImplicitSwappedPojo,ImplicitSwappedPojo[][][]> d
		) throws Exception {

		assertObject(a).json().is("'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'");
		assertObject(b).json().is("[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]");
		assertObject(c).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'}");
		assertObject(d).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]}");

		assertObject(a).isType(ImplicitSwappedPojo.class);
		assertObject(b[0][0][0]).isType(ImplicitSwappedPojo.class);
		assertObject(c.keySet().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(c.values().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(d.keySet().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(d.values().iterator().next()[0][0][0]).isType(ImplicitSwappedPojo.class);

		return "OK";
	}

	@RestMethod(method=GET, path="/enumHeaders")
	public String enumHeaders(
			@Header(n="a",cf="uon") TestEnum a,
			@Header(n="an",cf="uon") TestEnum an,
			@Header(n="b",cf="uon") TestEnum[][][] b,
			@Header(n="c",cf="uon") List<TestEnum> c,
			@Header(n="d",cf="uon") List<List<List<TestEnum>>> d,
			@Header(n="e",cf="uon") List<TestEnum[][][]> e,
			@Header(n="f",cf="uon") Map<TestEnum,TestEnum> f,
			@Header(n="g",cf="uon") Map<TestEnum,TestEnum[][][]> g,
			@Header(n="h",cf="uon") Map<TestEnum,List<TestEnum[][][]>> h
		) throws Exception {

		assertEquals(TestEnum.TWO, a);
		assertNull(an);
		assertObject(b).json().is("[[['TWO',null],null],null]");
		assertObject(c).json().is("['TWO',null]");
		assertObject(d).json().is("[[['TWO',null],null],null]");
		assertObject(e).json().is("[[[['TWO',null],null],null],null]");
		assertObject(f).json().is("{ONE:'TWO'}");
		assertObject(g).json().is("{ONE:[[['TWO',null],null],null]}");
		assertObject(h).json().is("{ONE:[[[['TWO',null],null],null],null]}");

		assertObject(c.get(0)).isType(TestEnum.class);
		assertObject(d.get(0).get(0).get(0)).isType(TestEnum.class);
		assertObject(e.get(0)).isType(TestEnum[][][].class);
		assertObject(f.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(f.values().iterator().next()).isType(TestEnum.class);
		assertObject(g.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(g.values().iterator().next()).isType(TestEnum[][][].class);
		assertObject(h.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(h.values().iterator().next().get(0)).isType(TestEnum[][][].class);

		return "OK";
	}

	@RestMethod(method=GET, path="/mapHeader")
	public String mapHeader(
		@Header("a") String a,
		@Header(name="b",allowEmptyValue=true) String b,
		@Header("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals("", b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/beanHeader")
	public String beanHeader(
		@Header("a") String a,
		@Header(name="b",allowEmptyValue=true) String b,
		@Header("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals("", b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/nameValuePairsHeader")
	public String nameValuePairsHeader(
		@Header("a") String a,
		@Header(name="b",allowEmptyValue=true) String b,
		@Header("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals("", b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/headerIfNE1")
	public String headerIfNE1(
		@Header("a") String a
	) throws Exception {

		assertEquals("foo", a);

		return "OK";
	}

	@RestMethod(method=GET, path="/headerIfNE2")
	public String headerIfNE2(
		@Header("a") String a
	) throws Exception {

		assertEquals(null, a);

		return "OK";
	}

	@RestMethod(method=GET, path="/headerIfNEMap")
	public String headerIfNEMap(
		@Header("a") String a,
		@Header("b") String b,
		@Header("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/headerIfNEBean")
	public String headerIfNEBean(
		@Header("a") String a,
		@Header("b") String b,
		@Header("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/headerIfNEnameValuePairs")
	public String headerIfNEnameValuePairs(
		@Header("a") String a,
		@Header("b") String b,
		@Header("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Query tests
	//-----------------------------------------------------------------------------------------------------------------

	@RestMethod(method=GET, path="/primitiveQueries")
	public String primitiveQueries(
			@Query("a") String a,
			@Query("an") String an,
			@Query("b") int b,
			@Query("c") Integer c,
			@Query("cn") Integer cn,
			@Query("d") Boolean d,
			@Query("e") float e,
			@Query("f") Float f
		) throws Exception {

		assertEquals(a, "foo");
		assertNull(an);
		assertEquals(123, b);
		assertEquals(123, (int)c);
		assertNull(cn);
		assertTrue(d);
		assertTrue(1f == e);
		assertTrue(1f == f);
		return "OK";
	}

	@RestMethod(method=GET, path="/primitiveCollectionQueries")
	public String primitiveCollectionQueries(
			@Query("a") int[][][] a,
			@Query("b") Integer[][][] b,
			@Query("c") String[][][] c,
			@Query("d") List<Integer> d,
			@Query("e") List<List<List<Integer>>> e,
			@Query("f") List<Integer[][][]> f,
			@Query("g") List<int[][][]> g,
			@Query("h") List<String> h
		) throws Exception {

		assertObject(a).json().is("[[[1,2],null],null]");
		assertObject(b).json().is("[[[1,null],null],null]");
		assertObject(c).json().is("[[['foo',null],null],null]");
		assertObject(d).json().is("[1,null]");
		assertObject(e).json().is("[[[1,null],null],null]");
		assertObject(f).json().is("[[[[1,null],null],null],null]");
		assertObject(g).json().is("[[[[1,2],null],null],null]");
		assertObject(h).json().is("['foo','bar',null]");

		assertObject(d.get(0)).isType(Integer.class);
		assertObject(e.get(0).get(0).get(0)).isType(Integer.class);
		assertObject(f.get(0)).isType(Integer[][][].class);
		assertObject(g.get(0)).isType(int[][][].class);

		return "OK";
	}

	@RestMethod(method=GET, path="/beanQueries")
	public String beanQueries(
			@Query(n="a",cf="uon") ABean a,
			@Query(n="an",cf="uon") ABean an,
			@Query(n="b",cf="uon") ABean[][][] b,
			@Query(n="c",cf="uon") List<ABean> c,
			@Query(n="d",cf="uon") List<ABean[][][]> d,
			@Query(n="e",cf="uon") Map<String,ABean> e,
			@Query(n="f",cf="uon") Map<String,List<ABean>> f,
			@Query(n="g",cf="uon") Map<String,List<ABean[][][]>> g,
			@Query(n="h",cf="uon") Map<Integer,List<ABean>> h
		) throws Exception {

		assertObject(a).json().is("{a:1,b:'foo'}");
		assertNull(an);
		assertObject(b).json().is("[[[{a:1,b:'foo'},null],null],null]");
		assertObject(c).json().is("[{a:1,b:'foo'},null]");
		assertObject(d).json().is("[[[[{a:1,b:'foo'},null],null],null],null]");
		assertObject(e).json().is("{foo:{a:1,b:'foo'}}");
		assertObject(f).json().is("{foo:[{a:1,b:'foo'}]}");
		assertObject(g).json().is("{foo:[[[[{a:1,b:'foo'},null],null],null],null]}");
		assertObject(h).json().is("{'1':[{a:1,b:'foo'}]}");

		assertObject(c.get(0)).isType(ABean.class);
		assertObject(d.get(0)).isType(ABean[][][].class);
		assertObject(e.get("foo")).isType(ABean.class);
		assertObject(f.get("foo").get(0)).isType(ABean.class);
		assertObject(g.get("foo").get(0)).isType(ABean[][][].class);
		assertObject(h.keySet().iterator().next()).isType(Integer.class);
		assertObject(h.values().iterator().next().get(0)).isType(ABean.class);
		return "OK";
	}

	@RestMethod(method=GET, path="/typedBeanQueries")
	public String typedBeanQueries(
			@Query(n="a",cf="uon") TypedBean a,
			@Query(n="an",cf="uon") TypedBean an,
			@Query(n="b",cf="uon") TypedBean[][][] b,
			@Query(n="c",cf="uon") List<TypedBean> c,
			@Query(n="d",cf="uon") List<TypedBean[][][]> d,
			@Query(n="e",cf="uon") Map<String,TypedBean> e,
			@Query(n="f",cf="uon") Map<String,List<TypedBean>> f,
			@Query(n="g",cf="uon") Map<String,List<TypedBean[][][]>> g,
			@Query(n="h",cf="uon") Map<Integer,List<TypedBean>> h
		) throws Exception {

		assertObject(a).json().is("{a:1,b:'foo'}");
		assertNull(an);
		assertObject(b).json().is("[[[{a:1,b:'foo'},null],null],null]");
		assertObject(c).json().is("[{a:1,b:'foo'},null]");
		assertObject(d).json().is("[[[[{a:1,b:'foo'},null],null],null],null]");
		assertObject(e).json().is("{foo:{a:1,b:'foo'}}");
		assertObject(f).json().is("{foo:[{a:1,b:'foo'}]}");
		assertObject(g).json().is("{foo:[[[[{a:1,b:'foo'},null],null],null],null]}");
		assertObject(h).json().is("{'1':[{a:1,b:'foo'}]}");

		assertObject(a).isType(TypedBeanImpl.class);
		assertObject(b[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(c.get(0)).isType(TypedBeanImpl.class);
		assertObject(d.get(0)[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(e.get("foo")).isType(TypedBeanImpl.class);
		assertObject(f.get("foo").get(0)).isType(TypedBeanImpl.class);
		assertObject(g.get("foo").get(0)[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(h.keySet().iterator().next()).isType(Integer.class);
		assertObject(h.get(1).get(0)).isType(TypedBeanImpl.class);

		return "OK";
	}

	@RestMethod(method=GET, path="/swappedPojoQueries")
	public String swappedPojoQueries(
			@Query(n="a",cf="uon") SwappedPojo a,
			@Query(n="b",cf="uon") SwappedPojo[][][] b,
			@Query(n="c",cf="uon") Map<SwappedPojo,SwappedPojo> c,
			@Query(n="d",cf="uon") Map<SwappedPojo,SwappedPojo[][][]> d
		) throws Exception {

		assertObject(a).json().is("'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'");
		assertObject(b).json().is("[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]");
		assertObject(c).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'}");
		assertObject(d).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]}");

		assertObject(a).isType(SwappedPojo.class);
		assertObject(b[0][0][0]).isType(SwappedPojo.class);
		assertObject(c.keySet().iterator().next()).isType(SwappedPojo.class);
		assertObject(c.values().iterator().next()).isType(SwappedPojo.class);
		assertObject(d.keySet().iterator().next()).isType(SwappedPojo.class);
		assertObject(d.values().iterator().next()[0][0][0]).isType(SwappedPojo.class);

		return "OK";
	}

	@RestMethod(method=GET, path="/implicitSwappedPojoQueries")
	public String implicitSwappedPojoQueries(
			@Query(n="a",cf="uon") ImplicitSwappedPojo a,
			@Query(n="b",cf="uon") ImplicitSwappedPojo[][][] b,
			@Query(n="c",cf="uon") Map<ImplicitSwappedPojo,ImplicitSwappedPojo> c,
			@Query(n="d",cf="uon") Map<ImplicitSwappedPojo,ImplicitSwappedPojo[][][]> d
		) throws Exception {

		assertObject(a).json().is("'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'");
		assertObject(b).json().is("[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]");
		assertObject(c).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'}");
		assertObject(d).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]}");

		assertObject(a).isType(ImplicitSwappedPojo.class);
		assertObject(b[0][0][0]).isType(ImplicitSwappedPojo.class);
		assertObject(c.keySet().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(c.values().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(d.keySet().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(d.values().iterator().next()[0][0][0]).isType(ImplicitSwappedPojo.class);

		return "OK";
	}

	@RestMethod(method=GET, path="/enumQueries")
	public String enumQueries(
			@Query(n="a",cf="uon") TestEnum a,
			@Query(n="an",cf="uon") TestEnum an,
			@Query(n="b",cf="uon") TestEnum[][][] b,
			@Query(n="c",cf="uon") List<TestEnum> c,
			@Query(n="d",cf="uon") List<List<List<TestEnum>>> d,
			@Query(n="e",cf="uon") List<TestEnum[][][]> e,
			@Query(n="f",cf="uon") Map<TestEnum,TestEnum> f,
			@Query(n="g",cf="uon") Map<TestEnum,TestEnum[][][]> g,
			@Query(n="h",cf="uon") Map<TestEnum,List<TestEnum[][][]>> h
		) throws Exception {

		assertEquals(TestEnum.TWO, a);
		assertNull(an);
		assertObject(b).json().is("[[['TWO',null],null],null]");
		assertObject(c).json().is("['TWO',null]");
		assertObject(d).json().is("[[['TWO',null],null],null]");
		assertObject(e).json().is("[[[['TWO',null],null],null],null]");
		assertObject(f).json().is("{ONE:'TWO'}");
		assertObject(g).json().is("{ONE:[[['TWO',null],null],null]}");
		assertObject(h).json().is("{ONE:[[[['TWO',null],null],null],null]}");

		assertObject(c.get(0)).isType(TestEnum.class);
		assertObject(d.get(0).get(0).get(0)).isType(TestEnum.class);
		assertObject(e.get(0)).isType(TestEnum[][][].class);
		assertObject(f.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(f.values().iterator().next()).isType(TestEnum.class);
		assertObject(g.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(g.values().iterator().next()).isType(TestEnum[][][].class);
		assertObject(h.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(h.values().iterator().next().get(0)).isType(TestEnum[][][].class);

		return "OK";
	}

	@RestMethod(method=GET, path="/stringQuery1")
	public String stringQuery1(
			@Query("a") int a,
			@Query("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	@RestMethod(method=GET, path="/stringQuery2")
	public String stringQuery2(
			@Query("a") int a,
			@Query("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	@RestMethod(method=GET, path="/mapQuery")
	public String mapQuery(
			@Query("a") int a,
			@Query("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	@RestMethod(method=GET, path="/beanQuery")
	public String beanQuery(
			@Query("a") String a,
			@Query(n="b",allowEmptyValue=true) String b,
			@Query("c") String c
		) throws Exception {

		assertEquals("foo", a);
		assertEquals("", b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/nameValuePairsQuery")
	public String nameValuePairsQuery(
		@Query("a") String a,
		@Query(n="b",allowEmptyValue=true) String b,
		@Query("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals("", b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/queryIfNE1")
	public String queryIfNE1(
		@Query("a") String a
	) throws Exception {

		assertEquals("foo", a);

		return "OK";
	}

	@RestMethod(method=GET, path="/queryIfNE2")
	public String queryIfNE2(
		@Query("q") String a
	) throws Exception {

		assertEquals(null, a);

		return "OK";
	}

	@RestMethod(method=GET, path="/queryIfNEMap")
	public String queryIfNEMap(
		@Query("a") String a,
		@Query("b") String b,
		@Query("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/queryIfNEBean")
	public String queryIfNEBean(
		@Query("a") String a,
		@Query("b") String b,
		@Query("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=GET, path="/queryIfNEnameValuePairs")
	public String queryIfNEnameValuePairs(
		@Query("a") String a,
		@Query("b") String b,
		@Query("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}


	//-----------------------------------------------------------------------------------------------------------------
	// FormData tests
	//-----------------------------------------------------------------------------------------------------------------

	@RestMethod(method=POST, path="/primitiveFormData")
	public String primitiveFormData(
			@FormData("a") String a,
			@FormData("an") String an,
			@FormData("b") int b,
			@FormData("c") Integer c,
			@FormData("cn") Integer cn,
			@FormData("d") Boolean d,
			@FormData("e") float e,
			@FormData("f") Float f
		) throws Exception {

		assertEquals("foo", a);
		assertNull(an);
		assertEquals(123, b);
		assertEquals(123, (int)c);
		assertNull(cn);
		assertTrue(d);
		assertTrue(1f == e);
		assertTrue(1f == f);
		return "OK";
	}

	@RestMethod(method=POST, path="/primitiveCollectionFormData")
	public String primitiveCollectionFormData(
			@FormData("a") int[][][] a,
			@FormData("b") Integer[][][] b,
			@FormData("c") String[][][] c,
			@FormData("d") List<Integer> d,
			@FormData("e") List<List<List<Integer>>> e,
			@FormData("f") List<Integer[][][]> f,
			@FormData("g") List<int[][][]> g,
			@FormData("h") List<String> h
		) throws Exception {

		assertObject(a).json().is("[[[1,2],null],null]");
		assertObject(b).json().is("[[[1,null],null],null]");
		assertObject(c).json().is("[[['foo',null],null],null]");
		assertObject(d).json().is("[1,null]");
		assertObject(e).json().is("[[[1,null],null],null]");
		assertObject(f).json().is("[[[[1,null],null],null],null]");
		assertObject(g).json().is("[[[[1,2],null],null],null]");
		assertObject(h).json().is("['foo','bar',null]");

		assertObject(d.get(0)).isType(Integer.class);
		assertObject(e.get(0).get(0).get(0)).isType(Integer.class);
		assertObject(f.get(0)).isType(Integer[][][].class);
		assertObject(g.get(0)).isType(int[][][].class);

		return "OK";
	}

	@RestMethod(method=POST, path="/beanFormData")
	public String beanFormData(
			@FormData(n="a",cf="uon") ABean a,
			@FormData(n="an",cf="uon") ABean an,
			@FormData(n="b",cf="uon") ABean[][][] b,
			@FormData(n="c",cf="uon") List<ABean> c,
			@FormData(n="d",cf="uon") List<ABean[][][]> d,
			@FormData(n="e",cf="uon") Map<String,ABean> e,
			@FormData(n="f",cf="uon") Map<String,List<ABean>> f,
			@FormData(n="g",cf="uon") Map<String,List<ABean[][][]>> g,
			@FormData(n="h",cf="uon") Map<Integer,List<ABean>> h
		) throws Exception {

		assertObject(a).json().is("{a:1,b:'foo'}");
		assertNull(an);
		assertObject(b).json().is("[[[{a:1,b:'foo'},null],null],null]");
		assertObject(c).json().is("[{a:1,b:'foo'},null]");
		assertObject(d).json().is("[[[[{a:1,b:'foo'},null],null],null],null]");
		assertObject(e).json().is("{foo:{a:1,b:'foo'}}");
		assertObject(f).json().is("{foo:[{a:1,b:'foo'}]}");
		assertObject(g).json().is("{foo:[[[[{a:1,b:'foo'},null],null],null],null]}");
		assertObject(h).json().is("{'1':[{a:1,b:'foo'}]}");

		assertObject(c.get(0)).isType(ABean.class);
		assertObject(d.get(0)).isType(ABean[][][].class);
		assertObject(e.get("foo")).isType(ABean.class);
		assertObject(f.get("foo").get(0)).isType(ABean.class);
		assertObject(g.get("foo").get(0)).isType(ABean[][][].class);
		assertObject(h.keySet().iterator().next()).isType(Integer.class);
		assertObject(h.values().iterator().next().get(0)).isType(ABean.class);
		return "OK";
	}

	@RestMethod(method=POST, path="/typedBeanFormData")
	public String typedBeanFormData(
			@FormData(n="a",cf="uon") TypedBean a,
			@FormData(n="an",cf="uon") TypedBean an,
			@FormData(n="b",cf="uon") TypedBean[][][] b,
			@FormData(n="c",cf="uon") List<TypedBean> c,
			@FormData(n="d",cf="uon") List<TypedBean[][][]> d,
			@FormData(n="e",cf="uon") Map<String,TypedBean> e,
			@FormData(n="f",cf="uon") Map<String,List<TypedBean>> f,
			@FormData(n="g",cf="uon") Map<String,List<TypedBean[][][]>> g,
			@FormData(n="h",cf="uon") Map<Integer,List<TypedBean>> h
		) throws Exception {

		assertObject(a).json().is("{a:1,b:'foo'}");
		assertNull(an);
		assertObject(b).json().is("[[[{a:1,b:'foo'},null],null],null]");
		assertObject(c).json().is("[{a:1,b:'foo'},null]");
		assertObject(d).json().is("[[[[{a:1,b:'foo'},null],null],null],null]");
		assertObject(e).json().is("{foo:{a:1,b:'foo'}}");
		assertObject(f).json().is("{foo:[{a:1,b:'foo'}]}");
		assertObject(g).json().is("{foo:[[[[{a:1,b:'foo'},null],null],null],null]}");
		assertObject(h).json().is("{'1':[{a:1,b:'foo'}]}");

		assertObject(a).isType(TypedBeanImpl.class);
		assertObject(b[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(c.get(0)).isType(TypedBeanImpl.class);
		assertObject(d.get(0)[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(e.get("foo")).isType(TypedBeanImpl.class);
		assertObject(f.get("foo").get(0)).isType(TypedBeanImpl.class);
		assertObject(g.get("foo").get(0)[0][0][0]).isType(TypedBeanImpl.class);
		assertObject(h.keySet().iterator().next()).isType(Integer.class);
		assertObject(h.get(1).get(0)).isType(TypedBeanImpl.class);

		return "OK";
	}

	@RestMethod(method=POST, path="/swappedPojoFormData")
	public String swappedPojoFormData(
			@FormData(n="a",cf="uon") SwappedPojo a,
			@FormData(n="b",cf="uon") SwappedPojo[][][] b,
			@FormData(n="c",cf="uon") Map<SwappedPojo,SwappedPojo> c,
			@FormData(n="d",cf="uon") Map<SwappedPojo,SwappedPojo[][][]> d
		) throws Exception {

		assertObject(a).json().is("'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'");
		assertObject(b).json().is("[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]");
		assertObject(c).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'}");
		assertObject(d).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]}");

		assertObject(a).isType(SwappedPojo.class);
		assertObject(b[0][0][0]).isType(SwappedPojo.class);
		assertObject(c.keySet().iterator().next()).isType(SwappedPojo.class);
		assertObject(c.values().iterator().next()).isType(SwappedPojo.class);
		assertObject(d.keySet().iterator().next()).isType(SwappedPojo.class);
		assertObject(d.values().iterator().next()[0][0][0]).isType(SwappedPojo.class);

		return "OK";
	}

	@RestMethod(method=POST, path="/implicitSwappedPojoFormData")
	public String implicitSwappedPojoFormData(
			@FormData(n="a",cf="uon") ImplicitSwappedPojo a,
			@FormData(n="b",cf="uon") ImplicitSwappedPojo[][][] b,
			@FormData(n="c",cf="uon") Map<ImplicitSwappedPojo,ImplicitSwappedPojo> c,
			@FormData(n="d",cf="uon") Map<ImplicitSwappedPojo,ImplicitSwappedPojo[][][]> d
		) throws Exception {

		assertObject(a).json().is("'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'");
		assertObject(b).json().is("[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]");
		assertObject(c).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/'}");
		assertObject(d).json().is("{'swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/':[[['swap-~!@#$%^&*()_+`-={}[]|:;\"<,>.?/',null],null],null]}");

		assertObject(a).isType(ImplicitSwappedPojo.class);
		assertObject(b[0][0][0]).isType(ImplicitSwappedPojo.class);
		assertObject(c.keySet().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(c.values().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(d.keySet().iterator().next()).isType(ImplicitSwappedPojo.class);
		assertObject(d.values().iterator().next()[0][0][0]).isType(ImplicitSwappedPojo.class);

		return "OK";
	}

	@RestMethod(method=POST, path="/enumFormData")
	public String enumFormData(
			@FormData(n="a",cf="uon") TestEnum a,
			@FormData(n="an",cf="uon") TestEnum an,
			@FormData(n="b",cf="uon") TestEnum[][][] b,
			@FormData(n="c",cf="uon") List<TestEnum> c,
			@FormData(n="d",cf="uon") List<List<List<TestEnum>>> d,
			@FormData(n="e",cf="uon") List<TestEnum[][][]> e,
			@FormData(n="f",cf="uon") Map<TestEnum,TestEnum> f,
			@FormData(n="g",cf="uon") Map<TestEnum,TestEnum[][][]> g,
			@FormData(n="h",cf="uon") Map<TestEnum,List<TestEnum[][][]>> h
		) throws Exception {

		assertEquals(TestEnum.TWO, a);
		assertNull(an);
		assertObject(b).json().is("[[['TWO',null],null],null]");
		assertObject(c).json().is("['TWO',null]");
		assertObject(d).json().is("[[['TWO',null],null],null]");
		assertObject(e).json().is("[[[['TWO',null],null],null],null]");
		assertObject(f).json().is("{ONE:'TWO'}");
		assertObject(g).json().is("{ONE:[[['TWO',null],null],null]}");
		assertObject(h).json().is("{ONE:[[[['TWO',null],null],null],null]}");

		assertObject(c.get(0)).isType(TestEnum.class);
		assertObject(d.get(0).get(0).get(0)).isType(TestEnum.class);
		assertObject(e.get(0)).isType(TestEnum[][][].class);
		assertObject(f.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(f.values().iterator().next()).isType(TestEnum.class);
		assertObject(g.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(g.values().iterator().next()).isType(TestEnum[][][].class);
		assertObject(h.keySet().iterator().next()).isType(TestEnum.class);
		assertObject(h.values().iterator().next().get(0)).isType(TestEnum[][][].class);

		return "OK";
	}

	@RestMethod(method=POST, path="/mapFormData")
	public String mapFormData(
		@FormData("a") String a,
		@FormData(n="b",aev=true) String b,
		@FormData("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals("", b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=POST, path="/beanFormData2")
	public String beanFormData(
		@FormData("a") String a,
		@FormData(n="b",aev=true) String b,
		@FormData("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals("", b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=POST, path="/nameValuePairsFormData")
	public String nameValuePairsFormData(
		@FormData("a") String a,
		@FormData(n="b",aev=true) String b,
		@FormData("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals("", b);
		//assertEquals(null, c);  // This is impossible to represent.

		return "OK";
	}

	@RestMethod(method=POST, path="/formDataIfNE1")
	public String formDataIfNE1(
		@FormData("a") String a
	) throws Exception {

		assertEquals("foo", a);

		return "OK";
	}

	@RestMethod(method=POST, path="/formDataIfNE2")
	public String formDataIfNE2(
		@FormData("a") String a
	) throws Exception {

		assertEquals(null, a);

		return "OK";
	}

	@RestMethod(method=POST, path="/formDataIfNEMap")
	public String formDataIfNEMap(
		@FormData("a") String a,
		@FormData("b") String b,
		@FormData("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=POST, path="/formDataIfNEBean")
	public String formDataIfNEBean(
		@FormData("a") String a,
		@FormData("b") String b,
		@FormData("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}

	@RestMethod(method=POST, path="/formDataIfNENameValuePairs")
	public String formDataIfNENameValuePairs(
		@FormData("a") String a,
		@FormData("b") String b,
		@FormData("c") String c
	) throws Exception {

		assertEquals("foo", a);
		assertEquals(null, b);
		assertEquals(null, c);

		return "OK";
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Path tests
	//-----------------------------------------------------------------------------------------------------------------

	@RestMethod(method=POST, path="/pathVars1/{a}/{b}")
	public String pathVars1(
		@Path("a") int a,
		@Path("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}


	@RestMethod(method=POST, path="/pathVars2/{a}/{b}")
	public String pathVars2(
		@Path("a") int a,
		@Path("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	@RestMethod(method=POST, path="/pathVars3/{a}/{b}")
	public String pathVars3(
		@Path("a") int a,
		@Path("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	//-----------------------------------------------------------------------------------------------------------------
	// @Request tests
	//-----------------------------------------------------------------------------------------------------------------

	@RestMethod(method=POST, path="/reqBeanPath/{a}/{b}")
	public String reqBeanPath(
		@Path("a") int a,
		@Path("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	@RestMethod(method=POST, path="/reqBeanQuery")
	public String reqBeanQuery(
		@Query("a") int a,
		@Query("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	@RestMethod(method=POST, path="/reqBeanQueryIfNE")
	public String reqBeanQueryIfNE(
		@Query("a") String a,
		@Query("b") String b,
		@Query("c") String c
		) throws Exception {

		assertEquals("foo", a);
		assertNull(b);
		assertNull(c);

		return "OK";
	}

	@RestMethod(method=POST, path="/reqBeanFormData")
	public String reqBeanFormData(
		@FormData("a") int a,
		@FormData("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	@RestMethod(method=POST, path="/reqBeanFormDataIfNE")
	public String reqBeanFormDataIfNE(
		@FormData("a") String a,
		@FormData("b") String b,
		@FormData("c") String c
		) throws Exception {

		assertEquals("foo", a);
		assertNull(b);
		assertNull(c);

		return "OK";
	}

	@RestMethod(method=POST, path="/reqBeanHeader")
	public String reqBeanHeader(
		@Header("a") int a,
		@Header("b") String b
		) throws Exception {

		assertEquals(1, a);
		assertEquals("foo", b);

		return "OK";
	}

	@RestMethod(method=POST, path="/reqBeanHeaderIfNE")
	public String reqBeanHeaderIfNE(
		@Header("a") String a,
		@Header("b") String b,
		@Header("c") String c
		) throws Exception {

		assertEquals("foo", a);
		assertNull(b);
		assertNull(c);

		return "OK";
	}
	//-----------------------------------------------------------------------------------------------------------------
	// Test return types.
	//-----------------------------------------------------------------------------------------------------------------

	// Various primitives

	@RestMethod(method=GET, path="/returnVoid")
	public void returnVoid() {
	}

	@RestMethod(method=GET, path="/returnInteger")
	public Integer returnInteger() {
		return 1;
	}

	@RestMethod(method=GET, path="/returnInt")
	public int returnInt() {
		return 1;
	}

	@RestMethod(method=GET, path="/returnBoolean")
	public boolean returnBoolean() {
		return true;
	}

	@RestMethod(method=GET, path="/returnFloat")
	public float returnFloat() {
		return 1f;
	}

	@RestMethod(method=GET, path="/returnFloatObject")
	public Float returnFloatObject() {
		return 1f;
	}

	@RestMethod(method=GET, path="/returnString")
	public String returnString() {
		return "foobar";
	}

	@RestMethod(method=GET, path="/returnNullString")
	public String returnNullString() {
		return null;
	}

	@RestMethod(method=GET, path="/returnInt3dArray")
	public int[][][] returnInt3dArray() {
		return new int[][][]{{{1,2},null},null};
	}

	@RestMethod(method=GET, path="/returnInteger3dArray")
	public Integer[][][] returnInteger3dArray() {
		return new Integer[][][]{{{1,null},null},null};
	}

	@RestMethod(method=GET, path="/returnString3dArray")
	public String[][][] returnString3dArray() {
		return new String[][][]{{{"foo","bar",null},null},null};
	}

	@RestMethod(method=GET, path="/returnIntegerList")
	public List<Integer> returnIntegerList() {
		return asList(new Integer[]{1,null});
	}

	@RestMethod(method=GET, path="/returnInteger3dList")
	public List<List<List<Integer>>> returnInteger3dList() {
		return AList.of(AList.of(AList.of(1,null),null),null);
	}

	@RestMethod(method=GET, path="/returnInteger1d3dList")
	public List<Integer[][][]> returnInteger1d3dList() {
		return AList.of(new Integer[][][]{{{1,null},null},null},null);
	}

	@RestMethod(method=GET, path="/returnInt1d3dList")
	public List<int[][][]> returnInt1d3dList() {
		return AList.of(new int[][][]{{{1,2},null},null},null);
	}

	@RestMethod(method=GET, path="/returnStringList")
	public List<String> returnStringList() {
		return asList(new String[]{"foo","bar",null});
	}

	// Beans

	@RestMethod(method=GET, path="/returnBean")
	public ABean returnBean() {
		return ABean.get();
	}

	@RestMethod(method=GET, path="/returnBean3dArray")
	public ABean[][][] returnBean3dArray() {
		return new ABean[][][]{{{ABean.get(),null},null},null};
	}

	@RestMethod(method=GET, path="/returnBeanList")
	public List<ABean> returnBeanList() {
		return asList(ABean.get());
	}

	@RestMethod(method=GET, path="/returnBean1d3dList")
	public List<ABean[][][]> returnBean1d3dList() {
		return AList.of(new ABean[][][]{{{ABean.get(),null},null},null},null);
	}

	@RestMethod(method=GET, path="/returnBeanMap")
	public Map<String,ABean> returnBeanMap() {
		return AMap.of("foo",ABean.get());
	}

	@RestMethod(method=GET, path="/returnBeanListMap")
	public Map<String,List<ABean>> returnBeanListMap() {
		return AMap.of("foo",asList(ABean.get()));
	}

	@RestMethod(method=GET, path="/returnBean1d3dListMap")
	public Map<String,List<ABean[][][]>> returnBean1d3dListMap() {
		return AMap.of("foo", AList.of(new ABean[][][]{{{ABean.get(),null},null},null},null));
	}

	@RestMethod(method=GET, path="/returnBeanListMapIntegerKeys")
	public Map<Integer,List<ABean>> returnBeanListMapIntegerKeys() {
		return AMap.of(1,asList(ABean.get()));
	}

	// Typed beans

	@RestMethod(method=GET, path="/returnTypedBean")
	public TypedBean returnTypedBean() {
		return TypedBeanImpl.get();
	}

	@RestMethod(method=GET, path="/returnTypedBean3dArray")
	public TypedBean[][][] returnTypedBean3dArray() {
		return new TypedBean[][][]{{{TypedBeanImpl.get(),null},null},null};
	}

	@RestMethod(method=GET, path="/returnTypedBeanList")
	public List<TypedBean> returnTypedBeanList() {
		return asList((TypedBean)TypedBeanImpl.get());
	}

	@RestMethod(method=GET, path="/returnTypedBean1d3dList")
	public List<TypedBean[][][]> returnTypedBean1d3dList() {
		return AList.of(new TypedBean[][][]{{{TypedBeanImpl.get(),null},null},null},null);
	}

	@RestMethod(method=GET, path="/returnTypedBeanMap")
	public Map<String,TypedBean> returnTypedBeanMap() {
		return AMap.of("foo",TypedBeanImpl.get());
	}

	@RestMethod(method=GET, path="/returnTypedBeanListMap")
	public Map<String,List<TypedBean>> returnTypedBeanListMap() {
		return AMap.of("foo",asList((TypedBean)TypedBeanImpl.get()));
	}

	@RestMethod(method=GET, path="/returnTypedBean1d3dListMap")
	public Map<String,List<TypedBean[][][]>> returnTypedBean1d3dListMap() {
		return AMap.of("foo", AList.of(new TypedBean[][][]{{{TypedBeanImpl.get(),null},null},null},null));
	}

	@RestMethod(method=GET, path="/returnTypedBeanListMapIntegerKeys")
	public Map<Integer,List<TypedBean>> returnTypedBeanListMapIntegerKeys() {
		return AMap.of(1,asList((TypedBean)TypedBeanImpl.get()));
	}

	// Swapped POJOs

	@RestMethod(method=GET, path="/returnSwappedPojo")
	public SwappedPojo returnSwappedPojo() {
		return new SwappedPojo();
	}

	@RestMethod(method=GET, path="/returnSwappedPojo3dArray")
	public SwappedPojo[][][] returnSwappedPojo3dArray() {
		return new SwappedPojo[][][]{{{new SwappedPojo(),null},null},null};
	}

	@RestMethod(method=GET, path="/returnSwappedPojoMap")
	public Map<SwappedPojo,SwappedPojo> returnSwappedPojoMap() {
		return AMap.of(new SwappedPojo(),new SwappedPojo());
	}

	@RestMethod(method=GET, path="/returnSwappedPojo3dMap")
	public Map<SwappedPojo,SwappedPojo[][][]> returnSwappedPojo3dMap() {
		return AMap.of(new SwappedPojo(),new SwappedPojo[][][]{{{new SwappedPojo(),null},null},null});
	}

	// Implicit swapped POJOs

	@RestMethod(method=GET, path="/returnImplicitSwappedPojo")
	public ImplicitSwappedPojo returnImplicitSwappedPojo() {
		return new ImplicitSwappedPojo();
	}

	@RestMethod(method=GET, path="/returnImplicitSwappedPojo3dArray")
	public ImplicitSwappedPojo[][][] returnImplicitSwappedPojo3dArray() {
		return new ImplicitSwappedPojo[][][]{{{new ImplicitSwappedPojo(),null},null},null};
	}

	@RestMethod(method=GET, path="/returnImplicitSwappedPojoMap")
	public Map<ImplicitSwappedPojo,ImplicitSwappedPojo> returnImplicitSwappedPojoMap() {
		return AMap.of(new ImplicitSwappedPojo(),new ImplicitSwappedPojo());
	}

	@RestMethod(method=GET, path="/returnImplicitSwappedPojo3dMap")
	public Map<ImplicitSwappedPojo,ImplicitSwappedPojo[][][]> returnImplicitSwappedPojo3dMap() {
		return AMap.of(new ImplicitSwappedPojo(),new ImplicitSwappedPojo[][][]{{{new ImplicitSwappedPojo(),null},null},null});
	}

	// Enums

	@RestMethod(method=GET, path="/returnEnum")
	public TestEnum returnEnum() {
		return TestEnum.TWO;
	}

	@RestMethod(method=GET, path="/returnEnum3d")
	public TestEnum[][][] returnEnum3d() {
		return new TestEnum[][][]{{{TestEnum.TWO,null},null},null};
	}

	@RestMethod(method=GET, path="/returnEnumList")
	public List<TestEnum> returnEnumList() {
		return AList.of(TestEnum.TWO,null);
	}

	@RestMethod(method=GET, path="/returnEnum3dList")
	public List<List<List<TestEnum>>> returnEnum3dList() {
		return AList.of(AList.of(AList.of(TestEnum.TWO,null),null),null);
	}

	@RestMethod(method=GET, path="/returnEnum1d3dList")
	public List<TestEnum[][][]> returnEnum1d3dList() {
		return AList.of(new TestEnum[][][]{{{TestEnum.TWO,null},null},null},null);
	}

	@RestMethod(method=GET, path="/returnEnumMap")
	public Map<TestEnum,TestEnum> returnEnumMap() {
		return AMap.of(TestEnum.ONE,TestEnum.TWO);
	}

	@RestMethod(method=GET, path="/returnEnum3dArrayMap")
	public Map<TestEnum,TestEnum[][][]> returnEnum3dArrayMap() {
		return AMap.of(TestEnum.ONE,new TestEnum[][][]{{{TestEnum.TWO,null},null},null});
	}

	@RestMethod(method=GET, path="/returnEnum1d3dListMap")
	public Map<TestEnum,List<TestEnum[][][]>> returnEnum1d3dListMap() {
		return AMap.of(TestEnum.ONE,AList.of(new TestEnum[][][]{{{TestEnum.TWO,null},null},null},null));
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Test parameters
	//-----------------------------------------------------------------------------------------------------------------

	// Various primitives

	@RestMethod(method=POST, path="/setInt")
	public void setInt(@Body int x) {
		assertEquals(1, x);
	}

	@RestMethod(method=POST, path="/setInteger")
	public void setInteger(@Body Integer x) {
		assertEquals((Integer)1, x);
	}

	@RestMethod(method=POST, path="/setBoolean")
	public void setBoolean(@Body boolean x) {
		assertTrue(x);
	}

	@RestMethod(method=POST, path="/setFloat")
	public void setFloat(@Body float x) {
		assertTrue(1f == x);
	}

	@RestMethod(method=POST, path="/setFloatObject")
	public void setFloatObject(@Body Float x) {
		assertTrue(1f == x);
	}

	@RestMethod(method=POST, path="/setString")
	public void setString(@Body String x) {
		assertEquals("foo", x);
	}

	@RestMethod(method=POST, path="/setNullString")
	public void setNullString(@Body String x) {
		assertNull(x);
	}

	@RestMethod(method=POST, path="/setInt3dArray")
	public String setInt3dArray(@Body int[][][] x) {
		return ""+x[0][0][0];
	}

	@RestMethod(method=POST, path="/setInteger3dArray")
	public void setInteger3dArray(@Body Integer[][][] x) {
		assertObject(x).json().is("[[[1,null],null],null]");
	}

	@RestMethod(method=POST, path="/setString3dArray")
	public void setString3dArray(@Body String[][][] x) {
		assertObject(x).json().is("[[['foo',null],null],null]");
	}

	@RestMethod(method=POST, path="/setIntegerList")
	public void setIntegerList(@Body List<Integer> x) {
		assertObject(x).json().is("[1,null]");
		assertObject(x.get(0)).isType(Integer.class);
	}

	@RestMethod(method=POST, path="/setInteger3dList")
	public void setInteger3dList(@Body List<List<List<Integer>>> x) {
		assertObject(x).json().is("[[[1,null],null],null]");
		assertObject(x.get(0).get(0).get(0)).isType(Integer.class);
	}

	@RestMethod(method=POST, path="/setInteger1d3dList")
	public void setInteger1d3dList(@Body List<Integer[][][]> x) {
		assertObject(x).json().is("[[[[1,null],null],null],null]");
		assertObject(x.get(0)).isType(Integer[][][].class);
		assertObject(x.get(0)[0][0][0]).isType(Integer.class);
	}

	@RestMethod(method=POST, path="/setInt1d3dList")
	public void setInt1d3dList(@Body List<int[][][]> x) {
		assertObject(x).json().is("[[[[1,2],null],null],null]");
		assertObject(x.get(0)).isType(int[][][].class);
	}

	@RestMethod(method=POST, path="/setStringList")
	public void setStringList(@Body List<String> x) {
		assertObject(x).json().is("['foo','bar',null]");
	}

	// Beans

	@RestMethod(method=POST, path="/setBean")
	public void setBean(@Body ABean x) {
		assertObject(x).json().is("{a:1,b:'foo'}");
	}

	@RestMethod(method=POST, path="/setBean3dArray")
	public void setBean3dArray(@Body ABean[][][] x) {
		assertObject(x).json().is("[[[{a:1,b:'foo'},null],null],null]");
	}

	@RestMethod(method=POST, path="/setBeanList")
	public void setBeanList(@Body List<ABean> x) {
		assertObject(x).json().is("[{a:1,b:'foo'}]");
	}

	@RestMethod(method=POST, path="/setBean1d3dList")
	public void setBean1d3dList(@Body List<ABean[][][]> x) {
		assertObject(x).json().is("[[[[{a:1,b:'foo'},null],null],null],null]");
	}

	@RestMethod(method=POST, path="/setBeanMap")
	public void setBeanMap(@Body Map<String,ABean> x) {
		assertObject(x).json().is("{foo:{a:1,b:'foo'}}");
	}

	@RestMethod(method=POST, path="/setBeanListMap")
	public void setBeanListMap(@Body Map<String,List<ABean>> x) {
		assertObject(x).json().is("{foo:[{a:1,b:'foo'}]}");
	}

	@RestMethod(method=POST, path="/setBean1d3dListMap")
	public void setBean1d3dListMap(@Body Map<String,List<ABean[][][]>> x) {
		assertObject(x).json().is("{foo:[[[[{a:1,b:'foo'},null],null],null],null]}");
	}

	@RestMethod(method=POST, path="/setBeanListMapIntegerKeys")
	public void setBeanListMapIntegerKeys(@Body Map<Integer,List<ABean>> x) {
		assertObject(x).json().is("{'1':[{a:1,b:'foo'}]}");  // Note: JsonSerializer serializes key as string.
		assertObject(x.keySet().iterator().next()).isType(Integer.class);
	}

	// Typed beans

	@RestMethod(method=POST, path="/setTypedBean")
	public void setTypedBean(@Body TypedBean x) {
		assertObject(x).json().is("{a:1,b:'foo'}");
		assertObject(x).isType(TypedBeanImpl.class);
	}

	@RestMethod(method=POST, path="/setTypedBean3dArray")
	public void setTypedBean3dArray(@Body TypedBean[][][] x) {
		assertObject(x).json().is("[[[{a:1,b:'foo'},null],null],null]");
		assertObject(x[0][0][0]).isType(TypedBeanImpl.class);
	}

	@RestMethod(method=POST, path="/setTypedBeanList")
	public void setTypedBeanList(@Body List<TypedBean> x) {
		assertObject(x).json().is("[{a:1,b:'foo'}]");
		assertObject(x.get(0)).isType(TypedBeanImpl.class);
	}

	@RestMethod(method=POST, path="/setTypedBean1d3dList")
	public void setTypedBean1d3dList(@Body List<TypedBean[][][]> x) {
		assertObject(x).json().is("[[[[{a:1,b:'foo'},null],null],null],null]");
		assertObject(x.get(0)[0][0][0]).isType(TypedBeanImpl.class);
	}

	@RestMethod(method=POST, path="/setTypedBeanMap")
	public void setTypedBeanMap(@Body Map<String,TypedBean> x) {
		assertObject(x).json().is("{foo:{a:1,b:'foo'}}");
		assertObject(x.get("foo")).isType(TypedBeanImpl.class);
	}

	@RestMethod(method=POST, path="/setTypedBeanListMap")
	public void setTypedBeanListMap(@Body Map<String,List<TypedBean>> x) {
		assertObject(x).json().is("{foo:[{a:1,b:'foo'}]}");
		assertObject(x.get("foo").get(0)).isType(TypedBeanImpl.class);
	}

	@RestMethod(method=POST, path="/setTypedBean1d3dListMap")
	public void setTypedBean1d3dListMap(@Body Map<String,List<TypedBean[][][]>> x) {
		assertObject(x).json().is("{foo:[[[[{a:1,b:'foo'},null],null],null],null]}");
		assertObject(x.get("foo").get(0)[0][0][0]).isType(TypedBeanImpl.class);
	}

	@RestMethod(method=POST, path="/setTypedBeanListMapIntegerKeys")
	public void setTypedBeanListMapIntegerKeys(@Body Map<Integer,List<TypedBean>> x) {
		assertObject(x).json().is("{'1':[{a:1,b:'foo'}]}");  // Note: JsonSerializer serializes key as string.
		assertObject(x.get(1).get(0)).isType(TypedBeanImpl.class);
	}

	// Swapped POJOs

	@RestMethod(method=POST, path="/setSwappedPojo")
	public void setSwappedPojo(@Body SwappedPojo x) {
		assertTrue(x.wasUnswapped);
	}

	@RestMethod(method=POST, path="/setSwappedPojo3dArray")
	public void setSwappedPojo3dArray(@Body SwappedPojo[][][] x) {
		assertObject(x).json().is("[[['"+SWAP+"',null],null],null]");
		assertTrue(x[0][0][0].wasUnswapped);
	}

	@RestMethod(method=POST, path="/setSwappedPojoMap")
	public void setSwappedPojoMap(@Body Map<SwappedPojo,SwappedPojo> x) {
		assertObject(x).json().is("{'"+SWAP+"':'"+SWAP+"'}");
		Map.Entry<SwappedPojo,SwappedPojo> e = x.entrySet().iterator().next();
		assertTrue(e.getKey().wasUnswapped);
		assertTrue(e.getValue().wasUnswapped);
	}

	@RestMethod(method=POST, path="/setSwappedPojo3dMap")
	public void setSwappedPojo3dMap(@Body Map<SwappedPojo,SwappedPojo[][][]> x) {
		assertObject(x).json().is("{'"+SWAP+"':[[['"+SWAP+"',null],null],null]}");
		Map.Entry<SwappedPojo,SwappedPojo[][][]> e = x.entrySet().iterator().next();
		assertTrue(e.getKey().wasUnswapped);
		assertTrue(e.getValue()[0][0][0].wasUnswapped);
	}

	// Implicit swapped POJOs

	@RestMethod(method=POST, path="/setImplicitSwappedPojo")
	public void setImplicitSwappedPojo(@Body ImplicitSwappedPojo x) {
		assertTrue(x.wasUnswapped);
	}

	@RestMethod(method=POST, path="/setImplicitSwappedPojo3dArray")
	public void setImplicitSwappedPojo3dArray(@Body ImplicitSwappedPojo[][][] x) {
		assertObject(x).json().is("[[['"+SWAP+"',null],null],null]");
		assertTrue(x[0][0][0].wasUnswapped);
	}

	@RestMethod(method=POST, path="/setImplicitSwappedPojoMap")
	public void setImplicitSwappedPojoMap(@Body Map<ImplicitSwappedPojo,ImplicitSwappedPojo> x) {
		assertObject(x).json().is("{'"+SWAP+"':'"+SWAP+"'}");
		Map.Entry<ImplicitSwappedPojo,ImplicitSwappedPojo> e = x.entrySet().iterator().next();
		assertTrue(e.getKey().wasUnswapped);
		assertTrue(e.getValue().wasUnswapped);
	}

	@RestMethod(method=POST, path="/setImplicitSwappedPojo3dMap")
	public void setImplicitSwappedPojo3dMap(@Body Map<ImplicitSwappedPojo,ImplicitSwappedPojo[][][]> x) {
		assertObject(x).json().is("{'"+SWAP+"':[[['"+SWAP+"',null],null],null]}");
		Map.Entry<ImplicitSwappedPojo,ImplicitSwappedPojo[][][]> e = x.entrySet().iterator().next();
		assertTrue(e.getKey().wasUnswapped);
		assertTrue(e.getValue()[0][0][0].wasUnswapped);
	}

	// Enums

	@RestMethod(method=POST, path="/setEnum")
	public void setEnum(@Body TestEnum x) {
		assertEquals(TestEnum.TWO, x);
	}

	@RestMethod(method=POST, path="/setEnum3d")
	public void setEnum3d(@Body TestEnum[][][] x) {
		assertObject(x).json().is("[[['TWO',null],null],null]");
	}

	@RestMethod(method=POST, path="/setEnumList")
	public void setEnumList(@Body List<TestEnum> x) {
		assertObject(x).json().is("['TWO',null]");
		assertObject(x.get(0)).isType(TestEnum.class);
	}

	@RestMethod(method=POST, path="/setEnum3dList")
	public void setEnum3dList(@Body List<List<List<TestEnum>>> x) {
		assertObject(x).json().is("[[['TWO',null],null],null]");
		assertObject(x.get(0).get(0).get(0)).isType(TestEnum.class);
	}

	@RestMethod(method=POST, path="/setEnum1d3dList")
	public void setEnum1d3dList(@Body List<TestEnum[][][]> x) {
		assertObject(x).json().is("[[[['TWO',null],null],null],null]");
		assertObject(x.get(0)).isType(TestEnum[][][].class);
	}

	@RestMethod(method=POST, path="/setEnumMap")
	public void setEnumMap(@Body Map<TestEnum,TestEnum> x) {
		assertObject(x).json().is("{ONE:'TWO'}");
		Map.Entry<TestEnum,TestEnum> e = x.entrySet().iterator().next();
		assertObject(e.getKey()).isType(TestEnum.class);
		assertObject(e.getValue()).isType(TestEnum.class);
	}

	@RestMethod(method=POST, path="/setEnum3dArrayMap")
	public void setEnum3dArrayMap(@Body Map<TestEnum,TestEnum[][][]> x) {
		assertObject(x).json().is("{ONE:[[['TWO',null],null],null]}");
		Map.Entry<TestEnum,TestEnum[][][]> e = x.entrySet().iterator().next();
		assertObject(e.getKey()).isType(TestEnum.class);
		assertObject(e.getValue()).isType(TestEnum[][][].class);
	}

	@RestMethod(method=POST, path="/setEnum1d3dListMap")
	public void setEnum1d3dListMap(@Body Map<TestEnum,List<TestEnum[][][]>> x) {
		assertObject(x).json().is("{ONE:[[[['TWO',null],null],null],null]}");
		Map.Entry<TestEnum,List<TestEnum[][][]>> e = x.entrySet().iterator().next();
		assertObject(e.getKey()).isType(TestEnum.class);
		assertObject(e.getValue().get(0)).isType(TestEnum[][][].class);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// PartFormatter tests
	//-----------------------------------------------------------------------------------------------------------------

	@RestMethod(method=POST, path="/partFormatters/{p1}")
	public String partFormatter(
		@Path("p1") String p1,
		@Header("h1") String h1,
		@Query("q1") String q1,
		@FormData("f1") String f1
	) throws Exception {

		assertEquals("dummy-1", p1);
		assertEquals("dummy-2", h1);
		assertEquals("dummy-3", q1);
		assertEquals("dummy-4", f1);

		return "OK";
	}

	//-----------------------------------------------------------------------------------------------------------------
	// @RemoteMethod(returns=HTTP_STATUS)
	//-----------------------------------------------------------------------------------------------------------------

	@RestMethod(method=GET, path="/httpStatusReturn200")
	public void httpStatusReturn200(RestResponse res) {
		res.setStatus(200);
	}

	@RestMethod(method=GET, path="/httpStatusReturn404")
	public void httpStatusReturn404(RestResponse res) {
		res.setStatus(404);
	}
}
