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
 * Used for fluent assertion calls against comparable objects.
 *
 * @param <R> The return type.
 */
@FluentSetters(returns="FluentComparableAssertion<R>")
@SuppressWarnings("rawtypes")
public class FluentComparableAssertion<R> extends FluentObjectAssertion<R> {

	private final Comparable value;

	/**
	 * Constructor.
	 *
	 * @param value The value being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentComparableAssertion(Comparable value, R returns) {
		super(value, returns);
		this.value = value;
	}

	/**
	 * Constructor.
	 *
	 * @param creator The assertion that created this assertion.
	 * @param value The value being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentComparableAssertion(Assertion creator, Comparable value, R returns) {
		super(creator, value, returns);
		this.value = value;
	}

	/**
	 * Asserts that the value is greater than the specified value.
	 *
	 * @param value The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isGreaterThan(Comparable value) throws AssertionError {
		exists();
		if (value != null && this.value.compareTo(value) < 0)
			throw error("Value was not greater than expected.\n\tExpected=[{0}]\n\tActual=[{1}]", value, this.value);
		return returns();
	}

	/**
	 * Asserts that the value is greater than the specified value.
	 *
	 * <p>
	 * Equivalent to {@link #isGreaterThan(Comparable)}
	 *
	 * @param value The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isGt(Comparable value) throws AssertionError {
		return isGreaterThan(value);
	}

	/**
	 * Asserts that the value is greater than or equal to the specified value.
	 *
	 * @param value The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isGreaterThanOrEquals(Comparable value) throws AssertionError {
		exists();
		if (value != null && this.value.compareTo(value) > 0)
				throw error("Value was not greater than or equals to expected.\n\tExpected=[{0}]\n\tActual=[{1}]", value, this.value);
		return returns();
	}

	/**
	 * Asserts that the value is greater than or equal to the specified value.
	 *
	 * <p>
	 * Equivalent to {@link #isGreaterThanOrEquals(Comparable)}
	 *
	 * @param value The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isGte(Comparable value) throws AssertionError {
		return isGreaterThanOrEquals(value);
	}

	/**
	 * Asserts that the value is less than the specified value.
	 *
	 * @param value The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isLessThan(Comparable value) throws AssertionError {
		exists();
		if (value != null && this.value.compareTo(value) <= 0)
				throw error("Value was not less than expected.\n\tExpected=[{0}]\n\tActual=[{1}]", value, this.value);
		return returns();
	}

	/**
	 * Asserts that the value is less than the specified value.
	 *
	 * <p>
	 * Equivalent to {@link #isLessThan(Comparable)}
	 *
	 * @param value The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isLt(Comparable value) throws AssertionError {
		return isLessThan(value);
	}

	/**
	 * Asserts that the value is less than or equals to the specified value.
	 *
	 * @param value The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isLessThanOrEquals(Comparable value) throws AssertionError {
		exists();
		if (value != null && this.value.compareTo(value) >= 0)
				throw error("Value was not less than or equals to expected.\n\tExpected=[{0}]\n\tActual=[{1}]", value, this.value);
		return returns();
	}

	/**
	 * Asserts that the value is less than or equals to the specified value.
	 *
	 * <p>
	 * Equivalent to {@link #isLessThanOrEquals(Comparable)}
	 *
	 * @param value The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isLte(Comparable value) throws AssertionError {
		return isLessThanOrEquals(value);
	}

	/**
	 * Asserts that the value is between (inclusive) the specified upper and lower values.
	 *
	 * @param lower The lower value to check against.
	 * @param upper The upper value to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isBetween(Comparable lower, Comparable upper) throws AssertionError {
		isLessThanOrEquals(upper);
		isGreaterThanOrEquals(lower);
		return returns();
	}
}
