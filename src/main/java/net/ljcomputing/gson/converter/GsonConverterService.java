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

package net.ljcomputing.gson.converter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Interface defining GSON converter service.
 * 
 * @author James G. Willmore
 *
 */
public interface GsonConverterService {

  /**
   * Transform given source Object to JSON.
   *
   * @param source the source
   * @return the string
   */
  String toJson(Object source);

  /**
   * From json.
   *
   * @param json the json
   * @param target the target
   * @return the object
   */
  Object fromJson(String json, Class<?> target);

  /**
   * From json.
   *
   * @param json the json
   * @param target the target
   * @return the list
   */
  @SuppressWarnings("rawtypes")
  List fromJson(String json, Type target);

  /**
   * Merge.
   *
   * @param to the to
   * @param from the from
   * @return the object
   */
  Object merge(Object to, Object from);

  /**
   * Merge.
   *
   * @param to the to
   * @param from the from
   * @param ignoredProperties the ignored properties
   * @return the object
   */
  Object merge(Object to, Object from, String ... ignoredProperties);
}
