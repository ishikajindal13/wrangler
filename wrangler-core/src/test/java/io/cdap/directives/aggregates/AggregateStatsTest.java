/*
 * Copyright © 2024 <Your Name or Company>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.junit.Test;
import org.junit.Assert;

import java.util.List;
import java.util.Arrays;

import io.cdap.wrangler.api.Row;
// import io.cdap.wrangler.testing.TestingRig;
import io.cdap.wrangler.test.TestingRig;


public class AggregateStatsTest {

  @Test
  public void testAggregateStatsDirective() throws Exception {
    List<Row> rows = Arrays.asList(
      new Row("data_transfer_size", "1MB").add("response_time", "2s"),
      new Row("data_transfer_size", "512KB").add("response_time", "500ms")
    );

    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    List<Row> results = TestingRig.execute(recipe, rows);

    Assert.assertEquals(1, results.size());

    double expectedMB = (1 * 1024 * 1024 + 512 * 1024) / (1024.0 * 1024.0);
    double expectedSec = (2 * 1000 + 500) / 1000.0;
    Assert.assertEquals(expectedMB, ((Number) results.get(0).getValue("total_size_mb")).doubleValue(), 0.001);
    Assert.assertEquals(expectedSec, ((Number) results.get(0).getValue("total_time_sec")).doubleValue(), 0.001);
  }
}