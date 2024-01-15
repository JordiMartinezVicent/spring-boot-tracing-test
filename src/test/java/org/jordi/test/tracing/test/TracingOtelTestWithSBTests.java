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
import org.jordi.test.tracing.test.TracingOtelTestWithSBTests.TracingTestWithSBTestConfig;
import org.jordi.tracing.test.TracingTest;
import org.jordi.tracing.test.collector.SpanCollector;
import org.jordi.tracing.test.extension.Spans;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = TracingTestWithSBTestConfig.class, properties = "tracing.provider=otel")
@TracingTest
// Exlusion only neede because we have both providers. In a real project only should be
// one of them
@EnableAutoConfiguration(exclude = BraveAutoConfiguration.class)
class TracingOtelTestWithSBTests {

	@Autowired
	private InstrumentedComponent instrumentedComponent;

	@Spans
	private SpanCollector spanCollector;

	@Test
	void smoke() {

		this.instrumentedComponent.doSomethingWithTrace();

		TracingAssertions.assertThat(this.spanCollector.getFinishedSpans())
			.hasNumberOfSpansEqualTo(1)
			.hasASpanWithName("instrumented-component-span");

	}

	@Test
	void assertTags() {

		this.instrumentedComponent.doSomethingWithTrace();

		TracingAssertions.assertThat(this.spanCollector.getFinishedSpans())
			.hasNumberOfSpansEqualTo(1)
			.assertThatASpanWithNameEqualTo("instrumented-component-span")
			.hasTag("tag", "tag-value");

	}

	static class InstrumentedComponent {

		@Autowired
		private Tracer tracer;

		void doSomethingWithTrace() {
			final Span rootSpan = this.tracer.nextSpan().name("instrumented-component-span").tag("tag", "tag-value");

			try (var spanInScope = this.tracer.withSpan(rootSpan.start())) {
				// Do something in span
			}
			finally {
				rootSpan.end();
			}
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class TracingTestWithSBTestConfig {

		@Bean
		InstrumentedComponent instrumentedComponent() {
			return new InstrumentedComponent();
		}

	}

}
