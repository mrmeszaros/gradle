/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.featurelifecycle

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static org.gradle.internal.featurelifecycle.SimulatedDeprecationMessageLogger.DIRECT_CALL
import static org.gradle.internal.featurelifecycle.SimulatedDeprecationMessageLogger.INDIRECT_CALL
import static org.gradle.internal.featurelifecycle.SimulatedDeprecationMessageLogger.INDIRECT_CALL_2

@Subject(FeatureUsage)
class FeatureUsageTest extends Specification {

    @Unroll
    def "stack is evaluated correctly for #callLocationClass.simpleName and #expectedMessage."() {
        expect:
        !usage.stack.empty
        usage.message == expectedMessage

        def stackTraceRoot = usage.stack[0]
        stackTraceRoot.className == callLocationClass.name
        stackTraceRoot.methodName == expectedMethod

        where:
        callLocationClass           | expectedMessage | expectedMethod | usage
        SimulatedJavaCallLocation   | DIRECT_CALL     | 'create'       | SimulatedJavaCallLocation.create()
        SimulatedJavaCallLocation   | INDIRECT_CALL   | 'indirectly'   | SimulatedJavaCallLocation.indirectly()
        SimulatedJavaCallLocation   | INDIRECT_CALL_2 | 'indirectly2'  | SimulatedJavaCallLocation.indirectly2()
        SimulatedGroovyCallLocation | DIRECT_CALL     | 'create'       | SimulatedGroovyCallLocation.create()
        SimulatedGroovyCallLocation | INDIRECT_CALL   | 'indirectly'   | SimulatedGroovyCallLocation.indirectly()
        SimulatedGroovyCallLocation | INDIRECT_CALL_2 | 'indirectly2'  | SimulatedGroovyCallLocation.indirectly2()
    }

    def "formats messages"() {
        expect:
        new FeatureUsage(message, warning, advice, getClass()).formattedMessage() == expected

        where:
        expected                 | message   | warning   | advice
        "message"                | "message" | null      | null
        "message warning"        | "message" | "warning" | null
        "message warning advice" | "message" | "warning" | "advice"
    }
}
