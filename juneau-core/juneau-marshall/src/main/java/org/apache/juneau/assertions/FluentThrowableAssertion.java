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

import java.util.function.*;

import org.apache.juneau.internal.*;

/**
 * Used for fluent assertion calls against throwables.
 *
 * @param <R> The return type.
 */
@FluentSetters(returns="FluentThrowableAssertion<R>")
public class FluentThrowableAssertion<R> extends FluentAssertion<R> {

	private final Throwable value;

	/**
	 * Constructor.
	 *
	 * @param value The throwable being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentThrowableAssertion(Throwable value, R returns) {
		this(null, value, returns);
	}

	/**
	 * Constructor.
	 *
	 * @param creator The assertion that created this assertion.
	 * @param value The throwable being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentThrowableAssertion(Assertion creator, Throwable value, R returns) {
		super(creator, returns);
		this.value = value;
	}

	/**
	 * Asserts that this throwable is of the specified type.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws a RuntimeException. </jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();})
	 * 		.isType(RuntimeException.<jk>class</jk>);
	 * </p>
	 *
	 * @param type The type.
	 * @return This object (for method chaining).
	 */
	public R isType(Class<?> type) {
		assertNotNull("type", type);
		if (! type.isInstance(value))
			throw error("Exception was not expected type.\n\tExpect=[{0}]\n\tActual=[{1}]", className(type), className(value));
		return returns();
	}

	/**
	 * Asserts that this throwable or any parent throwables contains all of the specified substrings.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws an exception with 'foobar' somewhere in the messages. </jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).contains(<js>"foobar"</js>);
	 * </p>
	 *
	 * @param substrings The substrings to check for.
	 * @return This object (for method chaining).
	 */
	public R contains(String...substrings) {
		assertNotNull("substrings", substrings);
		exists();
		for (String substring : substrings) {
			if (substring != null) {
				Throwable e2 = value;
				boolean found = false;
				while (e2 != null && ! found) {
					found |= StringUtils.contains(e2.getMessage(), substring);
					e2 = e2.getCause();
				}
				if (! found) {
					throw error("Exception message did not contain expected substring.\n\tSubstring=[{0}]\n\tText=[{1}]", substring, value.getMessage());
				}
			}
		}
		return returns();
	}

	/**
	 * Asserts that this throwable has the specified message.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws an exception with the message 'foobar'.</jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).is(<js>"foobar"</js>);
	 * </p>
	 *
	 * @param msg The message to check for.
	 * @return This object (for method chaining).
	 */
	public R is(String msg) {
		return message().is(msg);
	}

	/**
	 * Asserts that this throwable exists.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws any exception.</jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).exists();
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	public R exists() {
		if (value == null)
			throw error("Exception was not thrown.");
		return returns();
	}

	/**
	 * Asserts that this throwable doesn't exist.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method doesn't throw any exception.</jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).notExists();
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	public R doesNotExist() {
		if (value != null)
			throw error("Exception was thrown.");
		return returns();
	}

	/**
	 * Asserts that the value passes the specified predicate test.
	 *
	 * @param test The predicate to use to test the value.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R passes(Predicate<Throwable> test) throws AssertionError {
		if (! test.test(value))
			throw error("Value did not pass predicate test.\n\tValue=[{0}]", value);
		return returns();
	}

	/**
	 * Asserts that the value passes the specified predicate test.
	 *
	 * @param c The class to cast to for the predicate.
	 * @param <T> The class to cast to for the predicate.
	 * @param test The predicate to use to test the value.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Throwable> R passes(Class<T> c, Predicate<T> test) throws AssertionError {
		isType(c);
		if (! test.test((T) value))
			throw error("Value did not pass predicate test.\n\tValue=[{0}]", value);
		return returns();
	}

	/**
	 * Returns an assertion against the throwable message.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws an exception with 'foobar' somewhere in the messages. </jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).message().matches(<js>".*foobar.*"</js>);
	 * </p>
	 *
	 * @return An assertion against the throwable message.  Never <jk>null</jk>.
	 */
	public FluentStringAssertion<R> message() {
		return new FluentStringAssertion<>(this, value == null ? null : value.getMessage(), returns());
	}

	/**
	 * Returns an assertion against the throwable localized message.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws an exception with 'foobar' somewhere in the localized messages. </jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).localizedMessage().matches(<js>".*foobar.*"</js>);
	 * </p>
	 *
	 * @return An assertion against the throwable localized message.  Never <jk>null</jk>.
	 */
	public FluentStringAssertion<R> localizedMessage() {
		return new FluentStringAssertion<>(this, value == null ? null : value.getLocalizedMessage(), returns());
	}

	/**
	 * Returns an assertion against the throwable localized message.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws an exception with 'foobar' somewhere in the stack trace. </jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).stackTrace().contains(<js>"foobar"</js>);
	 * </p>
	 *
	 * @return An assertion against the throwable stacktrace.  Never <jk>null</jk>.
	 */
	public FluentStringAssertion<R> stackTrace() {
		return new FluentStringAssertion<>(this, value == null ? null : StringUtils.getStackTrace(value), returns());
	}

	/**
	 * Returns an assertion against the caused-by throwable.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws an exception whose caused-by message contains 'foobar'. </jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).causedBy().message().contains(<js>"foobar"</js>);
	 * </p>
	 *
	 * @return An assertion against the caused-by.  Never <jk>null</jk>.
	 */
	public FluentThrowableAssertion<R> causedBy() {
		return new FluentThrowableAssertion<>(this, value == null ? null : value.getCause(), returns());
	}

	/**
	 * Returns an assertion against the throwable localized message.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Asserts that the specified method throws an exception with a caused-by RuntimeException containing 'foobar'</jc>
	 * 	ThrowableAssertion.<jsm>assertThrown</jsm>(() -&gt; {foo.getBar();}).causedBy(RuntimeException.<jk>class</jk>).exists().contains(<js>"foobar"</js>);
	 * </p>
	 *
	 * @param throwableClass The class type to search for in the caused-by chain.
	 * @return An assertion against the caused-by throwable.  Never <jk>null</jk>.
	 */
	public FluentThrowableAssertion<R> find(Class<?> throwableClass) {
		Throwable t = value;
		while (t != null) {
			if (throwableClass.isInstance(t))
				return new FluentThrowableAssertion<>(this, t, returns());
			t = t.getCause();
		}
		return new FluentThrowableAssertion<>(this, null, returns());
	}

	// <FluentSetters>

	@Override /* GENERATED - Assertion */
	public FluentThrowableAssertion<R> msg(String msg, Object...args) {
		super.msg(msg, args);
		return this;
	}

	@Override /* GENERATED - Assertion */
	public FluentThrowableAssertion<R> stderr() {
		super.stderr();
		return this;
	}

	@Override /* GENERATED - Assertion */
	public FluentThrowableAssertion<R> stdout() {
		super.stdout();
		return this;
	}

	// </FluentSetters>
}
