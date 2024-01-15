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

package org.jordi.tracing.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jordi.tracing.test.extension.TracingExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;

/**
 * Annotation to be used for testing components with tracing.
 *
 * <p>
 * It allows the user to collect and assert the traces produced by components in an easy
 * way.
 * </p>
 *
 * <p>
 * Example of use in an amiga test:
 *
 * <pre>
 *
 * &#64;SpringBootTest(classes = TracingTestWithSBTestConfig.class)
 * &#64;TracingTest
 * &#64;EnableAutoConfiguration
 * class TracingTestWithSBTest {
 *
 *   &#64;Autowired
 *   private InstrumentedComponent instrumentedComponent;
 *
 *   &#64;Spans
 *   private SpanCollector spanCollector;
 *
 *   &#64;Test
 *   void smoke() {
 *
 *     this.instrumentedComponent.doSomethingWithTrace();
 *
 *     TracingAssertions.assertThat(this.spanCollector.getFinishedSpans())
 *         .hasNumberOfSpansEqualTo(1)
 *         .hasASpanWithName("instrumented-component-span");
 *   }
 * }
 * </pre>
 *
 * </p>
 *
 * @author Jordi Martinez Vicent
 * @since 1.0.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ImportAutoConfiguration
@AutoConfigureObservability
@ExtendWith(TracingExtension.class)
public @interface TracingTest {

}
