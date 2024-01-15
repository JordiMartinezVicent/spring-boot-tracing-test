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

package org.jordi.test.tracing.test;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.test.simple.TracingAssertions;
import org.jordi.tracing.test.TracingTest;
import org.jordi.tracing.test.collector.SpanCollector;
import org.jordi.tracing.test.extension.Spans;
import org.jordi.tracing.test.extension.TestTracer;
import org.junit.jupiter.api.Test;

@TracingTest
class TracingTestTests {

	@Spans
	private SpanCollector spanCollector;

	@TestTracer
	private Tracer tracer;

	@Test
	void smoke() {
		final Span rootSpan = this.tracer.nextSpan().name("rootSpan");

		try (var spanInScope = this.tracer.withSpan(rootSpan.start())) {
			// Do something in span
		}
		finally {
			rootSpan.end();
		}

		TracingAssertions.assertThat(this.spanCollector.getFinishedSpans())
			.hasNumberOfSpansEqualTo(1)
			.hasASpanWithName("rootSpan");

	}

	@Test
	void assertTags() {
		final Span rootSpan = this.tracer.nextSpan().name("rootSpan").tag("tag", "tag-value");

		try (var spanInScope = this.tracer.withSpan(rootSpan.start())) {
			// Do something in span
		}
		finally {
			rootSpan.end();
		}

		TracingAssertions.assertThat(this.spanCollector.getFinishedSpans())
			.hasNumberOfSpansEqualTo(1)
			.assertThatASpanWithNameEqualTo("rootSpan")
			.hasTag("tag", "tag-value");
	}

}
