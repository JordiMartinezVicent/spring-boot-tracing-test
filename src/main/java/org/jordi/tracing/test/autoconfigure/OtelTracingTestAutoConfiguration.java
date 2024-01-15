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

package org.jordi.tracing.test.autoconfigure;

import java.util.concurrent.atomic.AtomicInteger;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.jordi.tracing.test.test.collector.SpanCollector;
import org.jordi.tracing.test.test.collector.otel.OtelInMemoryExporterSpanCollector;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.tracing.SdkTracerProviderBuilderCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for tracing tests for otel implementation.
 *
 * @author Jordi Martinez Vicent
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(SdkTracerProvider.class)
// The ConditionalOnProperty is needed because at the example we use both providers. In a
// real application it won't be necessary
@ConditionalOnProperty(value = "tracing.provider", havingValue = "otel", matchIfMissing = true)
public class OtelTracingTestAutoConfiguration {

	private static AtomicInteger tracerCounter = new AtomicInteger(0);

	@Bean
	Tracer otelTracer(final SdkTracerProvider testSdkTracerProvider) {
		// Needed to not to reuse the same tracer for all tests as SB autowires the
		// same for all tests due to Opentelementry returns the
		// object stored at a static variables
		// Also we could do a GlobalOpenTelemetry.resetForTest(); but it could be
		// dangerous when execute tests in parallel
		final var counter = tracerCounter.getAndIncrement();

		return testSdkTracerProvider.tracerBuilder("test" + counter).build();
	}

	@Bean
	SdkTracerProvider testSdkTracerProvider(final InMemorySpanExporter testSpanExporter,
			final ObjectProvider<SpanProcessor> spanProcessors,
			final ObjectProvider<SdkTracerProviderBuilderCustomizer> customizers) {

		final SdkTracerProviderBuilder builder = SdkTracerProvider.builder();

		spanProcessors.orderedStream()
			.filter((spanProcessor) -> !(spanProcessor instanceof BatchSpanProcessor))
			.forEach(builder::addSpanProcessor);

		builder.addSpanProcessor(SimpleSpanProcessor.create(testSpanExporter));

		customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));

		return builder.build();
	}

	@Bean
	InMemorySpanExporter testSpanExporter() {
		return InMemorySpanExporter.create();
	}

	@Bean
	SpanCollector spanCollector(InMemorySpanExporter testSpanExporter) {
		return new OtelInMemoryExporterSpanCollector(testSpanExporter);
	}

}
