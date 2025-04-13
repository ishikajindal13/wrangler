
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

import io.cdap.wrangler.api.parser.ByteSize;

public class ByteSizeTest {
    @Test
    public void testByteSize() {
        ByteSize bs1 = new ByteSize("1KB");
        Assert.assertEquals(1024, bs1.getBytes());

        ByteSize bs2 = new ByteSize("1.5MB");
        Assert.assertEquals((long)(1.5 * 1024 * 1024), bs2.getBytes());

        ByteSize bs3 = new ByteSize("10GB");
        Assert.assertEquals(10L * 1024 * 1024 * 1024, bs3.getBytes());
    }
}
