/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

 package io.cdap.directives.aggregates;

 import java.util.Collections;
 import java.util.List;

 import io.cdap.cdap.api.annotation.Description;
 import io.cdap.cdap.api.annotation.Name;
 import io.cdap.cdap.api.annotation.Plugin;
 import io.cdap.wrangler.api.Arguments;
 import io.cdap.wrangler.api.Directive;
 import io.cdap.wrangler.api.DirectiveParseException;
 import io.cdap.wrangler.api.ExecutorContext;
 import io.cdap.wrangler.api.Row;
 import io.cdap.wrangler.api.parser.ColumnName;
 import io.cdap.wrangler.api.parser.Identifier;
 import io.cdap.wrangler.api.parser.TokenType;
 import io.cdap.wrangler.api.parser.UsageDefinition;
 
 /**
  * A directive that aggregates byte sizes and time durations from multiple rows
  * and outputs a single row containing total values.
  */
 @Plugin(type = Directive.TYPE)
 @Name("aggregate-stats")
 @Description("Aggregates total byte size and time duration over all rows and outputs a single row.")
 public class AggregateStats implements Directive {
     public static final String NAME = "aggregate-stats";
 
     private String sizeColumn;
     private String timeColumn;
     private String sizeOutputColumn;
     private String timeOutputColumn;
 
     private long totalBytes = 0;
     private long totalMilliseconds = 0;
 
     @Override
     public UsageDefinition define() {
         UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
         builder.define("sizeColumn", TokenType.COLUMN_NAME);
         builder.define("timeColumn", TokenType.COLUMN_NAME);
         builder.define("sizeOutputColumn", TokenType.IDENTIFIER);
         builder.define("timeOutputColumn", TokenType.IDENTIFIER);
         return builder.build();
     }
 
     @Override
     public void initialize(Arguments arguments) throws DirectiveParseException {
         this.sizeColumn = ((ColumnName) arguments.value("sizeColumn")).value();
         this.timeColumn = ((ColumnName) arguments.value("timeColumn")).value();
         this.sizeOutputColumn = ((Identifier) arguments.value("sizeOutputColumn")).value();
         this.timeOutputColumn = ((Identifier) arguments.value("timeOutputColumn")).value();
     }
 
     @Override
     public List<Row> execute(List<Row> rows, ExecutorContext context) {
         for (Row row : rows) {
             Object sizeVal = row.getValue(sizeColumn);
             Object timeVal = row.getValue(timeColumn);
 
             totalBytes += parseByteSize(sizeVal);
             totalMilliseconds += parseTimeDuration(timeVal);
         }
 
         Row result = new Row(sizeOutputColumn, convertBytesToMB(totalBytes))
                          .add(timeOutputColumn, convertMillisToSeconds(totalMilliseconds));
 
         return Collections.singletonList(result);
     }
 
     private long parseByteSize(Object val) {
         if (val == null) return 0;
         if (val instanceof Number) {
             return ((Number) val).longValue();
         }
         String str = val.toString().trim().toUpperCase();
         try {
             if (str.endsWith("KB")) {
                 return (long) (Double.parseDouble(str.replace("KB", "").trim()) * 1024);
             } else if (str.endsWith("MB")) {
                 return (long) (Double.parseDouble(str.replace("MB", "").trim()) * 1024 * 1024);
             } else if (str.endsWith("GB")) {
                 return (long) (Double.parseDouble(str.replace("GB", "").trim()) * 1024 * 1024 * 1024);
             } else {
                 return Long.parseLong(str); // assume bytes
             }
         } catch (NumberFormatException e) {
             return 0; // fallback on parse failure
         }
     }
 
     private long parseTimeDuration(Object val) {
         if (val == null) return 0;
         if (val instanceof Number) {
             return ((Number) val).longValue();
         }
         String str = val.toString().trim().toLowerCase();
         try {
             if (str.endsWith("ms")) {
                 return (long) (Double.parseDouble(str.replace("ms", "").trim()));
             } else if (str.endsWith("s")) {
                 return (long) (Double.parseDouble(str.replace("s", "").trim()) * 1000);
             } else if (str.endsWith("sec")) {
                 return (long) (Double.parseDouble(str.replace("sec", "").trim()) * 1000);
             } else {
                 return Long.parseLong(str); // assume milliseconds
             }
         } catch (NumberFormatException e) {
             return 0; // fallback on parse failure
         }
     }
 
     private double convertBytesToMB(long bytes) {
         return bytes / (1024.0 * 1024.0);
     }
 
     private double convertMillisToSeconds(long millis) {
         return millis / 1000.0;
     }
 
     @Override
     public void destroy() {
         // no cleanup needed
     }
   
 }