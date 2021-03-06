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
package org.apache.juneau.http.header;

import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class ContentType_Other_Test {

	@Test
	public void a01_basic() {
		ContentType ct = ContentType.of("application/json");
		assertEquals("application/json", ct.getValue());
	}

	@Test
	public void a02_getParameter() {
		ContentType ct = ContentType.of("application/json;charset=foo");
		assertEquals("foo", ct.getParameter("charset"));
		ct = ContentType.of(" application/json ; charset = foo ");
		assertEquals("foo", ct.getParameter("charset"));

		assertNull(ct.getParameter("x"));
	}
}
