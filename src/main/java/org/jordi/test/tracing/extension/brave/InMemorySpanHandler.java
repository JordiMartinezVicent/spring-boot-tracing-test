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

package org.jordi.test.tracing.extension.brave;

import java.util.ArrayList;
import java.util.List;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.internal.Nullable;
import brave.propagation.TraceContext;

/**
 *
 * {@link SpanHandler} to store the spans in-memory.
 *
 * @author Jordi Martinez Vicent
 * @since 1.0.0
 */
public class InMemorySpanHandler extends SpanHandler {

	final List<MutableSpan> spans = new ArrayList<>();

	public MutableSpan get(int i) {
		return this.spans.get(i);
	}

	public List<MutableSpan> spans() {
		return this.spans;
	}

	@Override
	public boolean begin(TraceContext context, MutableSpan span, @Nullable TraceContext parent) {
		return true;
	}

	@Override
	public boolean end(TraceContext context, MutableSpan span, Cause cause) {
		this.spans.add(span);
		return true;
	}

	public void clear() {
		this.spans.clear();
	}

	@Override
	public String toString() {
		return "TestSpanHandler{" + this.spans + "}";
	}

}
