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
package org.apache.juneau.assertions;

import org.apache.juneau.internal.*;

/**
 * Used for assertion calls against arrays.
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bcode w800'>
 * 	String[] <jv>array</jv> = <jk>new</jk> String[]{<js>"foo"</js>};
 * 	<jsm>assertArray</jsm>(<jv>array</jv>).exists().isSize(1);
 * </p>
 */
@FluentSetters(returns="ArrayAssertion")
public class ArrayAssertion extends FluentArrayAssertion<ArrayAssertion> {

	/**
	 * Creator.
	 *
	 * @param value The object being wrapped.
	 * @return A new {@link ArrayAssertion} object.
	 */
	public static ArrayAssertion create(Object value) {
		return new ArrayAssertion(value);
	}

	/**
	 * Creator.
	 *
	 * @param value The object being wrapped.
	 */
	public ArrayAssertion(Object value) {
		super(value, null);
	}

	@Override
	protected ArrayAssertion returns() {
		return this;
	}

	// <FluentSetters>

	@Override /* GENERATED - Assertion */
	public ArrayAssertion msg(String msg, Object...args) {
		super.msg(msg, args);
		return this;
	}

	@Override /* GENERATED - Assertion */
	public ArrayAssertion stderr() {
		super.stderr();
		return this;
	}

	@Override /* GENERATED - Assertion */
	public ArrayAssertion stdout() {
		super.stdout();
		return this;
	}

	// </FluentSetters>
}
