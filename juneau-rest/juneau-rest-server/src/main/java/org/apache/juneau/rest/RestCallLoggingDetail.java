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
package org.apache.juneau.rest;

/**
 * Represents the amount of detail to include in a log entry for HTTP requests and responses.
 */
public enum RestCallLoggingDetail {

	/**
	 * Log only the request and response status lines.
	 */
	SHORT,

	/**
	 * Log status lines and also headers.
	 */
	MEDIUM,

	/**
	 * Log status lines, headers, and bodies if available.
	 */
	LONG;

	boolean isOneOf(RestCallLoggingDetail...values) {
		for (RestCallLoggingDetail v : values)
			if (v == this)
				return true;
		return false;
	}
}
