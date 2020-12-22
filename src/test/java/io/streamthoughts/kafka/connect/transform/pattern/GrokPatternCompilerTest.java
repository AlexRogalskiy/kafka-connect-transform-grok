/*
 * Copyright 2020 StreamThoughts.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.kafka.connect.transform.pattern;

import io.streamthoughts.kafka.connect.transform.data.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GrokPatternCompilerTest {

    private GrokPatternCompiler compiler;

    @Before
    public void setUp() {
        compiler = new GrokPatternCompiler(new GrokPatternResolver(), false);
    }

    @Test
    public void shouldCompileMatcherGivenSingleGrokPattern() {
        final GrokMatcher matcher = compiler.compile("%{ISO8601_TIMEZONE}");
        Assert.assertNotNull(matcher);
        Assert.assertEquals("ISO8601_TIMEZONE", matcher.getGrokPattern(0).syntax());
        Assert.assertEquals("(?<ISO8601_TIMEZONE>(?:Z|[+-](?<HOUR>(?:2[0123]|[01]?[0-9]))(?::?(?<MINUTE>(?:[0-5][0-9])))))", matcher.expression());
    }

    @Test
    public void shouldCompileMatcherGivenMultipleGrokPatterns() {
        final GrokMatcher matcher = compiler.compile("%{ISO8601_TIMEZONE} %{LOGLEVEL} %{GREEDYDATA}");
        Assert.assertNotNull(matcher);
        Assert.assertNotNull(matcher.getGrokPattern("ISO8601_TIMEZONE"));
        Assert.assertNotNull(matcher.getGrokPattern("LOGLEVEL"));
        Assert.assertNotNull(matcher.getGrokPattern("GREEDYDATA"));
        Assert.assertEquals("(?<ISO8601_TIMEZONE>(?:Z|[+-](?<HOUR>(?:2[0123]|[01]?[0-9]))(?::?(?<MINUTE>(?:[0-5][0-9]))))) (?<LOGLEVEL>([Aa]lert|ALERT|[Tt]race|TRACE|[Dd]ebug|DEBUG|[Nn]otice|NOTICE|[Ii]nfo|INFO|[Ww]arn?(?:ing)?|WARN?(?:ING)?|[Ee]rr?(?:or)?|ERR?(?:OR)?|[Cc]rit?(?:ical)?|CRIT?(?:ICAL)?|[Ff]atal|FATAL|[Ss]evere|SEVERE|EMERG(?:ENCY)?|[Ee]merg(?:ency)?)) (?<GREEDYDATA>.*)", matcher.expression());
    }

    @Test
    public void shouldCompileMatcherGivenMultipleGrokPatternWithSemantic() {
        final GrokMatcher matcher = compiler.compile("%{ISO8601_TIMEZONE:timezone}");
        Assert.assertNotNull(matcher);
        Assert.assertEquals("ISO8601_TIMEZONE", matcher.getGrokPattern(0).syntax());
        Assert.assertEquals("timezone", matcher.getGrokPattern(0).semantic());
        Assert.assertEquals("(?<timezone>(?:Z|[+-](?<HOUR>(?:2[0123]|[01]?[0-9]))(?::?(?<MINUTE>(?:[0-5][0-9])))))", matcher.expression());
    }

    @Test
    public void shouldCompileMatcherGivenMultipleGrokPatternWithSemanticAndType() {
        final GrokMatcher matcher = compiler.compile("%{ISO8601_TIMEZONE:timezone:integer}");
        Assert.assertNotNull(matcher);
        Assert.assertEquals("ISO8601_TIMEZONE", matcher.getGrokPattern(0).syntax());
        Assert.assertEquals("timezone", matcher.getGrokPattern(0).semantic());
        Assert.assertEquals(Type.INTEGER, matcher.getGrokPattern(0).type());
        Assert.assertEquals("(?<timezone>(?:Z|[+-](?<HOUR>(?:2[0123]|[01]?[0-9]))(?::?(?<MINUTE>(?:[0-5][0-9])))))", matcher.expression());
    }

    @Test
    public void shouldCompileMatcherGivenCustomGrokPattern() {
        final GrokMatcher matcher = compiler.compile("(?<email>^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$)");
        Assert.assertNotNull(matcher);
        Assert.assertEquals("(?<email>^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$)", matcher.expression());
    }
}