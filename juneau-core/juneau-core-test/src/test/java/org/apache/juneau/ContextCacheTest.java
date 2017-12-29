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
package org.apache.juneau;

import static org.apache.juneau.TestUtils.*;
import static org.junit.Assert.*;

import org.junit.*;

/**
 * Tests the ContextCache class.
 */
public class ContextCacheTest {
	
	//-------------------------------------------------------------------------------------------------------------------
	// Basic tests
	//-------------------------------------------------------------------------------------------------------------------
	
	@Test
	public void testBasic() {
		
		PropertyStoreBuilder psb = PropertyStore.create();
		PropertyStore ps = psb.build();
		
		A a = ContextCache.INSTANCE.create(A.class, ps);
		B b = ContextCache.INSTANCE.create(B.class, ps);
		C c = ContextCache.INSTANCE.create(C.class, ps);
		
		assertObjectEquals("{f1:'xxx'}", a);
		assertObjectEquals("{f1:'xxx',f2:-1}", b);
		assertObjectEquals("{f1:'xxx',f2:-1,f3:false}", c);
		
		A a2 = ContextCache.INSTANCE.create(A.class, ps);
		B b2 = ContextCache.INSTANCE.create(B.class, ps);
		C c2 = ContextCache.INSTANCE.create(C.class, ps);
		
		assertTrue(a == a2);
		assertTrue(b == b2);
		assertTrue(c == c2);
		
		psb.set("A.f1", "foo");
		ps = psb.build();
		
		a2 = ContextCache.INSTANCE.create(A.class, ps);
		b2 = ContextCache.INSTANCE.create(B.class, ps);
		c2 = ContextCache.INSTANCE.create(C.class, ps);

		assertObjectEquals("{f1:'foo'}", a2);
		assertObjectEquals("{f1:'foo',f2:-1}", b2);
		assertObjectEquals("{f1:'foo',f2:-1,f3:false}", c2);
		
		assertTrue(a != a2);
		assertTrue(b != b2);
		assertTrue(c != c2);
		
		a = a2; b = b2; c = c2;
		
		ps = psb.set("B.f2.i", 123).build();
		
		a2 = ContextCache.INSTANCE.create(A.class, ps);
		b2 = ContextCache.INSTANCE.create(B.class, ps);
		c2 = ContextCache.INSTANCE.create(C.class, ps);

		assertObjectEquals("{f1:'foo'}", a2);
		assertObjectEquals("{f1:'foo',f2:123}", b2);
		assertObjectEquals("{f1:'foo',f2:123,f3:false}", c2);
		
		assertTrue(a == a2);
		assertTrue(b != b2);
		assertTrue(c != c2);

		a = a2; b = b2; c = c2;
		
		ps = psb.set("C.f3.b", true).build();
		
		a2 = ContextCache.INSTANCE.create(A.class, ps);
		b2 = ContextCache.INSTANCE.create(B.class, ps);
		c2 = ContextCache.INSTANCE.create(C.class, ps);

		assertObjectEquals("{f1:'foo'}", a2);
		assertObjectEquals("{f1:'foo',f2:123}", b2);
		assertObjectEquals("{f1:'foo',f2:123,f3:true}", c2);
		
		assertTrue(a == a2);
		assertTrue(b == b2);
		assertTrue(c != c2);

		a = a2; b = b2; c = c2;
		
		ps = psb.set("D.bad.o", "xxx").build();
		
		a2 = ContextCache.INSTANCE.create(A.class, ps);
		b2 = ContextCache.INSTANCE.create(B.class, ps);
		c2 = ContextCache.INSTANCE.create(C.class, ps);

		assertObjectEquals("{f1:'foo'}", a2);
		assertObjectEquals("{f1:'foo',f2:123}", b2);
		assertObjectEquals("{f1:'foo',f2:123,f3:true}", c2);
		
		assertTrue(a == a2);
		assertTrue(b == b2);
		assertTrue(c == c2);
		
		assertTrue(a.getPropertyStore() == a2.getPropertyStore());
		assertTrue(b.getPropertyStore() == b2.getPropertyStore());
		assertTrue(c.getPropertyStore() == c2.getPropertyStore());
		
		a2 = ContextCache.INSTANCE.create(A.class, a.getPropertyStore().builder().set("A.f1", "foo").build());
		assertTrue(a == a2);
		
		a2 = ContextCache.INSTANCE.create(A.class, a.getPropertyStore().builder().set("A.f1", "bar").build());
		assertTrue(a != a2);
	}

	
	public static class A extends Context {
		public final String f1;

		public A(PropertyStore ps) {
			super(ps);
			f1 = getProperty("A.f1", String.class, "xxx");
		}

		@Override
		public Session createSession(SessionArgs args) {
			return null;
		}

		@Override
		public SessionArgs createDefaultSessionArgs() {
			return null;
		}
	}
	
	public static class B extends A {
		public int f2;

		public B(PropertyStore ps) {
			super(ps);
			f2 = getProperty("B.f2.i", Integer.class, -1);
			
		}
	}
	
	public static class C extends B {
		public boolean f3;
		public C(PropertyStore ps) {
			super(ps);
			f3 = getProperty("C.f3.b", boolean.class, false);
		}
	}
	
	@Test
	public void testBadConstructor() {
		PropertyStoreBuilder psb = PropertyStore.create();
		PropertyStore ps = psb.build();		
	
		try {
			ContextCache.INSTANCE.create(D1.class, ps);
			fail("Exception expected");
		} catch (Exception e) {
			assertEquals("Could not create instance of class 'org.apache.juneau.ContextCacheTest$D1'", e.getLocalizedMessage());
		}
		try {
			ContextCache.INSTANCE.create(D2.class, ps);
			fail("Exception expected");
		} catch (Exception e) {
			assertEquals("Could not create instance of class 'org.apache.juneau.ContextCacheTest$D2'", e.getLocalizedMessage());
		}
	}
	
	public static class D1 extends A {
		protected D1(PropertyStore ps) {
			super(ps);
		}
	}

	public static class D2 extends A {
		public D2(PropertyStore ps) {
			super(ps);
			throw new RuntimeException("Error!");
		}
	}
}
