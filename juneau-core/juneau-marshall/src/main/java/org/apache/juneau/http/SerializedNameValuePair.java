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
package org.apache.juneau.http;

import static org.apache.juneau.internal.StringUtils.*;

import java.util.function.*;

import org.apache.http.*;
import org.apache.juneau.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.oapi.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.urlencoding.*;

/**
 * Subclass of {@link NameValuePair} for serializing POJOs as URL-encoded form post entries using the
 * {@link UrlEncodingSerializer class}.
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bcode w800'>
 * 	NameValuePairs params = <jk>new</jk> NameValuePairs()
 * 		.append(<jk>new</jk> SerializedNameValuePair(<js>"myPojo"</js>, pojo, UrlEncodingSerializer.<jsf>DEFAULT_SIMPLE</jsf>))
 * 		.append(<jk>new</jk> BasicNameValuePair(<js>"someOtherParam"</js>, <js>"foobar"</js>));
 * 	request.setEntity(<jk>new</jk> UrlEncodedFormEntity(params));
 * </p>
 */
public class SerializedNameValuePair extends BasicNameValuePair implements Headerable {
	private final Object value;
	private HttpPartType type;
	private HttpPartSerializerSession serializer;
	private HttpPartSchema schema = HttpPartSchema.DEFAULT;
	private boolean skipIfEmpty;

	/**
	 * Instantiates a new instance of this object.
	 *
	 * @return A new instance of this object.
	 */
	public static SerializedNameValuePair of(String name, Object value) {
		return new SerializedNameValuePair(name, value, null, null, null, false);
	}

	/**
	 * Instantiates a new instance of this object.
	 *
	 * @return A new instance of this object.
	 */
	public static SerializedNameValuePair of(String name, Supplier<?> value) {
		return new SerializedNameValuePair(name, value, null, null, null, false);
	}

	/**
	 * Constructor.
	 *
	 * @param name The parameter name.
	 * @param value The POJO to serialize to the parameter value.
	 * @param type The HTTP part type.
	 * @param serializer
	 * 	The serializer to use for serializing the value to a string value.
	 * @param schema
	 * 	The schema object that defines the format of the output.
	 * 	<br>If <jk>null</jk>, defaults to the schema defined on the serializer.
	 * 	<br>If that's also <jk>null</jk>, defaults to {@link HttpPartSchema#DEFAULT}.
	 * 	<br>Only used if serializer is schema-aware (e.g. {@link OpenApiSerializer}).
	 * @param skipIfEmpty If value is a blank string, the value should return as <jk>null</jk>.
	 */
	public SerializedNameValuePair(String name, Object value, HttpPartType type, HttpPartSerializerSession serializer, HttpPartSchema schema, boolean skipIfEmpty) {
		super(name, value);
		this.value = value;
		this.type = type;
		this.serializer = serializer;
		this.schema = schema;
		this.skipIfEmpty = skipIfEmpty;
	}

	/**
	 * Sets the HTTP part type.
	 *
	 * @param value The new value for this property.
	 * @return This object (for method chaining).
	 */
	public SerializedNameValuePair type(HttpPartType value) {
		this.type = value;
		return this;
	}

	/**
	 * Sets the serializer to use for serializing the value to a string value.
	 *
	 * @param value The new value for this property.
	 * @return This object (for method chaining).
	 */
	public SerializedNameValuePair serializer(HttpPartSerializer value) {
		if (value != null)
			return serializer(value.createPartSession(null));
		return this;
	}

	/**
	 * Sets the serializer to use for serializing the value to a string value.
	 *
	 * @param value The new value for this property.
	 * @return This object (for method chaining).
	 */
	public SerializedNameValuePair serializer(HttpPartSerializerSession value) {
		return serializer(value, true);
	}

	/**
	 * Sets the serializer to use for serializing the value to a string value.
	 *
	 * @param value The new value for this property.
	 * @param overwrite If <jk>true</jk>, overwrites the existing value if the old value is <jk>null</jk>.
	 * @return This object (for method chaining).
	 */
	public SerializedNameValuePair serializer(HttpPartSerializerSession value, boolean overwrite) {
		if (overwrite || serializer == null)
			this.serializer = value;
		return this;
	}

	/**
	 * Sets the schema object that defines the format of the output.
	 *
	 * @param value The new value for this property.
	 * @return This object (for method chaining).
	 */
	public SerializedNameValuePair schema(HttpPartSchema value) {
		this.schema = value;
		return this;
	}

	/**
	 * Don't serialize this pair if the value is <jk>null</jk> or an empty string.
	 *
	 * @return This object (for method chaining).
	 */
	public SerializedNameValuePair skipIfEmpty() {
		return skipIfEmpty(true);
	}

	/**
	 * Don't serialize this pair if the value is <jk>null</jk> or an empty string.
	 *
	 * @param value The new value of this setting.
	 * @return This object (for method chaining).
	 */
	public SerializedNameValuePair skipIfEmpty(boolean value) {
		this.skipIfEmpty = value;
		return this;
	}

	@Override /* Headerable */
	public SerializedHeader asHeader() {
		return new SerializedHeader(getName(), value, serializer, schema, skipIfEmpty);
	}

	@Override /* NameValuePair */
	public String getValue() {
		try {
			Object v = unwrap(value);
			HttpPartSchema schema = this.schema == null ? HttpPartSchema.DEFAULT : this.schema;
			String def = schema.getDefault();
			if (v == null) {
				if (def == null && ! schema.isRequired())
					return null;
				if (def == null && schema.isAllowEmptyValue())
					return null;
			}
			if (isEmpty(v) && skipIfEmpty && def == null)
				return null;
			return serializer == null ? stringify(v) : serializer.serialize(type, schema, v);
		} catch (SchemaValidationException e) {
			throw new BasicRuntimeException(e, "Validation error on request {0} parameter ''{1}''=''{2}''", type, getName(), value);
		} catch (SerializeException e) {
			throw new BasicRuntimeException(e, "Serialization error on request {0} parameter ''{1}''", type, getName());
		}
	}

	@Override /* Object */
	public String toString() {
		return getName() + "=" + getValue();
	}

	private Object unwrap(Object o) {
		if (o instanceof Supplier)
			return ((Supplier<?>)o).get();
		return o;
	}

	// <FluentSetters>

	// </FluentSetters>
}
