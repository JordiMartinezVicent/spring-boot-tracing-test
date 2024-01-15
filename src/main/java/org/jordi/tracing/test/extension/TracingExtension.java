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

package org.jordi.tracing.test.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Supplier;

import io.micrometer.tracing.BaggageManager;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jordi.tracing.test.test.collector.SpanCollector;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.SingletonSupplier;

/**
 * Extension to testing tracing components.
 *
 * <p>
 * It is intended to be use with TracingTest
 * </p>
 *
 * @author Jordi Martinez Vicent
 * @since 1.0.0
 */
public class TracingExtension implements AfterEachCallback, BeforeAllCallback, AfterAllCallback, ParameterResolver,
		TestInstancePostProcessor {

	private SpanCollector spanCollector;

	private ApplicationContext appContext;

	private SingletonSupplier<Tracer> tracerSupplier = new SingletonSupplier<>(this::getTracer, null);

	@Override
	public void beforeAll(final ExtensionContext context) throws Exception {
		GlobalOpenTelemetry.resetForTest();

		this.appContext = SpringExtension.getApplicationContext(context);
		this.spanCollector = this.appContext.getBean(SpanCollector.class);

	}

	@Override
	public void afterEach(final ExtensionContext context) throws Exception {
		this.spanCollector.reset();

	}

	@Override
	public void afterAll(final ExtensionContext context) throws Exception {
		this.spanCollector.close();
	}

	@Override
	public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return parameterContext.isAnnotated(Spans.class);
	}

	@Override
	public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
			throws ParameterResolutionException {

		return this.spanCollector;
	}

	@Override
	public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) throws Exception {

		for (final Field field : FieldUtils.getAllFields(testInstance.getClass())) {
			this.injectIfNeeded(field, Spans.class, testInstance, this.spanCollector);
			this.injectIfNeeded(field, TestTracer.class, testInstance, Tracer.class, this.tracerSupplier);
		}

	}

	private void injectIfNeeded(final Field field, final Class<? extends Annotation> annotation,
			final Object testInstance, final Object toInject) {

		if (field.isAnnotationPresent(annotation) && field.getType().isAssignableFrom(toInject.getClass())) {
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, testInstance, toInject);
		}
	}

	private <T> void injectIfNeeded(final Field field, final Class<? extends Annotation> annotation,
			final Object testInstance, final Class<T> toInjectType, final Supplier<T> toInject) {

		if (field.isAnnotationPresent(annotation) && field.getType().isAssignableFrom(toInjectType)) {

			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, testInstance, toInject.get());

		}
	}

	private Tracer getTracer() {

		try {
			return this.appContext.getBean(Tracer.class);
		}
		catch (final NoSuchBeanDefinitionException ex) {

			// needed for tests which does not use spring contexts
			// TODO: See if it is really necessary and, if it is, decouple it from the
			// otel implementation
			final var otelTracer = this.appContext.getBean(io.opentelemetry.api.trace.Tracer.class);
			return new OtelTracer(otelTracer, new OtelCurrentTraceContext(), (event) -> {
			}, BaggageManager.NOOP);

		}

	}

}
