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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.exporter.SpanReporter;

/**
 * @author jordimar
 */
public class TestSpanReporter implements SpanReporter {

	private final Queue<FinishedSpan> spans = new ConcurrentLinkedQueue<>();

	/**
	 * Polls stored spans for the latest entry.
	 * @return latest stored span
	 */
	public FinishedSpan poll() {
		return this.spans.poll();
	}

	/**
	 * Returns collected spans.
	 * @return collected spans
	 */
	public Queue<FinishedSpan> spans() {
		return new ConcurrentLinkedQueue<>(this.spans);
	}

	@Override
	public void report(FinishedSpan span) {
		this.spans.add(span);
	}

	@Override
	public void close() throws Exception {
		this.spans.clear();
	}

}
