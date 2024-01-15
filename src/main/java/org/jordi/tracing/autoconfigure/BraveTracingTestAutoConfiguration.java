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

package org.jordi.tracing.autoconfigure;

import brave.handler.SpanHandler;
import brave.sampler.Sampler;
import org.jordi.tracing.test.collector.SpanCollector;
import org.jordi.tracing.test.collector.brave.BraveInMemorySpanHandlerCollector;
import org.jordi.tracing.test.collector.brave.InMemorySpanHandler;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for tracing tests for brave implementation.
 *
 * @author Jordi Martinez Vicent
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(SpanHandler.class)
// The ConditionalOnProperty is needed because at the example we use both providers. In a
// real application it won't be necessary
@ConditionalOnProperty(value = "tracing.provider", havingValue = "brave", matchIfMissing = true)
public class BraveTracingTestAutoConfiguration {

	@Bean
	SpanHandler testSpanHandler() {
		return new InMemorySpanHandler();
	}

	@Bean
	SpanCollector spanCollector(InMemorySpanHandler spanHandler) {
		return new BraveInMemorySpanHandlerCollector(spanHandler);
	}

	// Needed to execute the SpanHandler
	@Bean
	Sampler braveSampler() {
		return Sampler.ALWAYS_SAMPLE;
	}

}
