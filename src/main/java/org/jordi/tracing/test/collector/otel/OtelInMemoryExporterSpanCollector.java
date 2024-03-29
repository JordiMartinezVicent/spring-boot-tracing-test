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

package org.jordi.tracing.test.collector.otel;

import java.util.List;

import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.otel.bridge.OtelFinishedSpan;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import org.jordi.tracing.test.collector.SpanCollector;

/**
 * {@link SpanCollector} which returns the spans exported by
 * a{@link InMemorySpanExporter}.
 *
 * @author Jordi Martinez Vicent
 * @since 1.0.0
 */
public class OtelInMemoryExporterSpanCollector implements SpanCollector {

	private final InMemorySpanExporter inMemorySpanExporter;

	/**
	 * Constructor.
	 * @param inMemorySpanExporter the InMemorySpanExporter
	 */
	public OtelInMemoryExporterSpanCollector(final InMemorySpanExporter inMemorySpanExporter) {
		this.inMemorySpanExporter = inMemorySpanExporter;
	}

	@Override
	public List<FinishedSpan> getFinishedSpans() {
		return this.inMemorySpanExporter.getFinishedSpanItems().stream().map(OtelFinishedSpan::fromOtel).toList();
	}

	@Override
	public void reset() {
		this.inMemorySpanExporter.reset();
	}

	@Override
	public void close() {
		this.inMemorySpanExporter.close();
	}

}
