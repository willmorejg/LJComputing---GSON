/**
           Copyright 2015, James G. Willmore

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.ljcomputing.gson.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import net.ljcomputing.gson.annotation.ExcludeFromJson;

/**
 * Exclude from Json annotation Gson exclusion strategy.
 * 
 * @author James G. Willmore
 *
 */
public class ExcludeFromJsonAnnotationExclusionStrategy implements ExclusionStrategy {

  /**
   * @see com.google.gson.ExclusionStrategy
   * #shouldSkipField(com.google.gson.FieldAttributes)
   */
  @Override
  public boolean shouldSkipField(final FieldAttributes fieldAttributes) {
    return fieldAttributes.getAnnotation(ExcludeFromJson.class) != null;
  }

  /**
   * @see com.google.gson.ExclusionStrategy#shouldSkipClass(java.lang.Class)
   */
  @Override
  public boolean shouldSkipClass(final Class<?> clazz) {
    return false;
  }
}
