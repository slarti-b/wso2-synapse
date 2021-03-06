/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.synapse.commons.staxon.core.json.stream.util;


import org.apache.synapse.commons.staxon.core.json.stream.JsonStreamTarget;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class CustomRegexMatchReplaceIgnoreAutoPrimitiveTarget extends StreamTargetDelegate {
  private final Pattern number = Pattern.compile("^-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]+)?$");
  private final boolean convertAttributes;
  private final Pattern customReplaceRegex;
  private final String customReplaceSequence;
  private String lastName;

  public CustomRegexMatchReplaceIgnoreAutoPrimitiveTarget(JsonStreamTarget delegate, boolean convertAttributes,
                                                          String customReplaceRegex, String customReplaceSequence) {
    super(delegate);
    this.convertAttributes = convertAttributes;
    this.customReplaceRegex = Pattern.compile(customReplaceRegex);
    this.customReplaceSequence = customReplaceSequence;
  }

  @Override
  public void name(String name) throws IOException {
    lastName = name;
    super.name(name);
  }

  @Override
  public void value(Object value) throws IOException {
    if (value instanceof String && (convertAttributes || !lastName.startsWith("@"))) {
      if (customReplaceRegex != null && customReplaceRegex.matcher(value.toString()).lookingAt()) {
        super.value(customReplaceRegex.matcher(value.toString()).replaceFirst(customReplaceSequence));
      } else if ("true".equals(value)) {
        super.value(Boolean.TRUE);
      } else if ("false".equals(value)) {
        super.value(Boolean.FALSE);
      } else if ("null".equals(value)) {
        super.value(null);
      } else if (number.matcher(value.toString()).matches()) {
        super.value(new BigDecimal(value.toString()));
      } else {
        super.value(value);
      }
    } else {
      super.value(value);
    }
  }
}

