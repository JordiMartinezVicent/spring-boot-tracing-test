/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jordi.tracing.test.collector;

import java.util.List;

import io.micrometer.tracing.exporter.FinishedSpan;

/**
 * Component to collect the spans created at the test.
 *
 * @author Jordi Martinez Vicent
 * @since 1.0.0
 */
public interface SpanCollector {

	/**
	 * Returns the collected spans.
	 * @return the collected spans
	 */
	List<FinishedSpan> getFinishedSpans();

	/**
	 * Clears the internal {@code List} of finished {@code Span}s.
	 */
	void reset();

	/**
	 * Closes this {@link SpanCollector}, releasing any resources.
	 */
	void close();

}
