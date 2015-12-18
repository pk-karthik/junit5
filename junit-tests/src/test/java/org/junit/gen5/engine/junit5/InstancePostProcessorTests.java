/*
 * Copyright 2015 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.gen5.engine.junit5;

import static java.util.Arrays.asList;
import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.engine.TestPlanSpecification.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.gen5.api.AfterEach;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Nested;
import org.junit.gen5.api.Test;
import org.junit.gen5.api.extension.AfterEachExtensionPoint;
import org.junit.gen5.api.extension.BeforeEachExtensionPoint;
import org.junit.gen5.api.extension.ExtendWith;
import org.junit.gen5.api.extension.ExtensionRegistrar;
import org.junit.gen5.api.extension.ExtensionRegistry;
import org.junit.gen5.api.extension.InstancePostProcessor;
import org.junit.gen5.api.extension.TestExtensionContext;
import org.junit.gen5.engine.TestPlanSpecification;

/**
 * Integration tests that verify support for {@link org.junit.gen5.api.extension.InstancePostProcessor}.
 */
public class InstancePostProcessorTests extends AbstractJUnit5TestEngineTestCase {

	@org.junit.Test
	public void instancePostProcessorInTopLevelClass() {
		TestPlanSpecification spec = build(forClass(OuterTestCase.class));

		TrackingEngineExecutionListener listener = executeTests(spec, 4);

		assertEquals(2, listener.testStartedCount.get(), "# tests started");
		assertEquals(2, listener.testSucceededCount.get(), "# tests succeeded");

		// @formatter:off
		assertEquals(asList(

			//NestedTestCase
			"fooPostProcessTestInstance", "barPostProcessTestInstance", "beforeMethod", "beforeInnerMethod", "testInner",

			//OuterTestCase
			"fooPostProcessTestInstance", "beforeMethod", "testOuter"

		), callSequence, "wrong call sequence");
		// @formatter:on
	}

	// -------------------------------------------------------------------

	private static List<String> callSequence = new ArrayList<>();

	@ExtendWith({ FooInstancePostProcessor.class })
	private static class OuterTestCase {

		@BeforeEach
		void beforeEach() {
			callSequence.add("beforeMethod");
		}

		@Test
		void testOuter() {
			callSequence.add("testOuter");
		}

		@Nested
		@ExtendWith(BarInstancePostProcessor.class)
		class InnerTestCase {
			@BeforeEach
			void beforeInnerMethod() {
				callSequence.add("beforeInnerMethod");
			}

			@Test
			void testInner() {
				callSequence.add("testInner");
			}
		}

	}

	private static class FooInstancePostProcessor implements InstancePostProcessor {

		@Override
		public void postProcessTestInstance(TestExtensionContext context) throws Exception {
			callSequence.add("fooPostProcessTestInstance");
		}
	}

	private static class BarInstancePostProcessor implements InstancePostProcessor {

		@Override
		public void postProcessTestInstance(TestExtensionContext context) throws Exception {
			callSequence.add("barPostProcessTestInstance");
		}
	}

}
