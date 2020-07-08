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

import static org.apache.juneau.internal.StringUtils.*;

import org.apache.juneau.*;
import org.apache.juneau.internal.*;

/**
 * Base class for all assertion objects.
 */
public class Assertion {

	String msg;
	Object[] msgArgs;
	boolean stdout, stderr;

	/**
	 * No-arg constructor.
	 */
	protected Assertion() {}

	/**
	 * Constructor used when this assertion is being created from within another assertion.
	 * @param creator The creator of this assertion.
	 */
	protected Assertion(Assertion creator) {
		this.msg = creator.msg;
		this.msgArgs = creator.msgArgs;
		this.stdout = creator.stdout;
		this.stderr = creator.stderr;
	}

	/**
	 * Allows to to specify the assertion failure message.
	 *
	 * <p>
	 * String can contain <js>"{msg}"</js> to represent the original message.
	 *
	 * @param msg The assertion failure message.
	 * @param args Optional message arguments.
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public Assertion msg(String msg, Object...args) {
		this.msg = msg.replace("{msg}", "<<<MSG>>>");
		this.msgArgs = args;
		return this;
	}

	/**
	 * If an error occurs, send the error message to STDOUT.
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public Assertion stdout() {
		this.stdout = true;
		return this;
	}

	/**
	 * If an error occurs, send the error message to STDERR.
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public Assertion stderr() {
		this.stderr = true;
		return this;
	}

	/**
	 * Creates a new {@link BasicAssertionError}.
	 *
	 * @param msg The message.
	 * @param args The message arguments.
	 * @return A new {@link BasicAssertionError}.
	 */
	protected BasicAssertionError error(String msg, Object...args) {
		msg = format(msg, args);
		if (this.msg != null)
			msg = format(this.msg, this.msgArgs).replace("<<<MSG>>>", msg);
		if (stdout)
			System.out.println(msg);
		if (stderr)
			System.err.println(msg);
		return new BasicAssertionError(msg);
	}

	/**
	 * Convenience method for getting the class name for an object.
	 *
	 * @param o The object to get the class name for.
	 * @return The class name for an object.
	 */
	protected static String className(Object o) {
		return ClassUtils.className(o);
	}

	/**
	 * Casts the specified object to the specified type.
	 *
	 * @param <T> The type to cast the object to.
	 * @param c The type to cast the object to.
	 * @param o The object to cast.
	 * @return The same object cast to the specified object.s
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T cast(Class<T> c, Object o) {
		if (o == null)
			return null;
		if (c.isInstance(o))
			return (T)o;
		throw new BasicAssertionError("Object was not expected type.  Expected={0}, actual={1}.", c.getClass().getName(), o.getClass().getName());
	}

	// <FluentSetters>

	// </FluentSetters>
}
