/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cdap.wrangler.parser;

import org.junit.Assert;
import org.junit.Test;

import io.cdap.wrangler.api.parser.TimeDuration;

public class TimeDurationTest {
    @Test
    public void testTimeDuration() {
        TimeDuration t1 = new TimeDuration("500ms");
        Assert.assertEquals(500_000_000, t1.getNanoseconds());

        TimeDuration t2 = new TimeDuration("2s");
        Assert.assertEquals(2_000_000_000L, t2.getNanoseconds());

        TimeDuration t3 = new TimeDuration("1.5m");
        Assert.assertEquals((long)(1.5 * 60 * 1_000_000_000L), t3.getNanoseconds());
    }
}
