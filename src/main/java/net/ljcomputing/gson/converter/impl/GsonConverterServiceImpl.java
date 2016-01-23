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

package net.ljcomputing.gson.converter.impl;

import net.ljcomputing.gson.converter.GsonConverterService;
import net.ljcomputing.gson.strategy.ExcludeFromJsonAnnotationExclusionStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * GSON converter service implementation.
 * 
 * @author James G. Willmore
 *
 */
@Service
public class GsonConverterServiceImpl implements GsonConverterService {
  /** The logger. */
  private static Logger logger = LoggerFactory
      .getLogger(GsonConverterServiceImpl.class);

  /** The static Gson instance. */
  private static Gson gson;

  /**
   * Instantiates a new gson converter service impl.
   */
  public GsonConverterServiceImpl() {
    gson = new GsonBuilder()
        .setExclusionStrategies(
            new ExcludeFromJsonAnnotationExclusionStrategy())
        .serializeNulls().create();
  }

  /**
   * @see net.ljcomputing.gson.converter.GsonConverterService#toJson(java.lang.Object)
   */
  public final String toJson(final Object source) {
    return gson.toJson(source);
  }

  /**
   * @see net.ljcomputing.gson.converter.GsonConverterService#fromJson(java.lang.String,
   *      java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  public final Object fromJson(final String json,
      @SuppressWarnings("rawtypes") final Class target) {
    return gson.fromJson(json, target);
  }

  /**
   * @see net.ljcomputing.gson.converter.GsonConverterService#fromJson(java.lang.String,
   *      java.lang.reflect.Type)
   */
  @SuppressWarnings("rawtypes")
  public final List fromJson(final String json, final Type target) {
    return gson.fromJson(json, target);
  }

  /**
   * @see net.ljcomputing.gson.converter.GsonConverterService#merge(java.lang.Object,
   *      java.lang.Object)
   */
  public final Object merge(final Object to, final Object from) {
    Class<?> toClass = to.getClass();
    Class<?> fromClass = from.getClass();
    
    if(!toClass.equals(fromClass)) {
      throw new RuntimeException("cannot merge to and from -- they are not the same class");
    }
    
    return merge(to, from, null);
  }

  /**
   * @see net.ljcomputing.gson.converter.GsonConverterService#merge(java.lang.Object,
   *      java.lang.Object, java.lang.String[])
   */
  public final Object merge(final Object to, final Object from,
      final String[] ignoredProperties) {
    try {
      mergeValues(getKeysFromJson(toJson(from)), to, from, ignoredProperties);
    } catch (Exception e) {
      logger.error("Exception occured while merging {} with {}", from, to, e);
    }

    return to;
  }

  /**
   * Find field.
   *
   * @param clazz
   *            the clazz
   * @param fieldName
   *            the field name
   * @return the field
   */
  private Field findField(final Class<?> clazz, final String fieldName) {
    Class<?> current = clazz;

    do {
      try {
        return current.getDeclaredField(fieldName);
      } catch (Exception e) {
        logger.debug(
            "exception ignored while getting declared field {} for class {}: {}",
            fieldName, clazz, e.getMessage());
      }
    } while ((current = current.getSuperclass()) != null);

    return null;
  }

  /**
   * Merge values.
   *
   * @param keysFrom            the keys from
   * @param to            the to
   * @param from            the from
   * @param ignoredProperties the ignored properties
   */
  @SuppressWarnings("rawtypes")
  private void mergeValues(final List keysFrom, final Object to,
      final Object from, final String[] ignoredProperties) {
    Class toClass = to.getClass();
    Class fromClass = from.getClass();

    if (null != ignoredProperties) {
      Arrays.sort(ignoredProperties);
    }

    for (Object key : keysFrom) {
      try {
        if (null != ignoredProperties
            && Arrays.binarySearch(ignoredProperties, key.toString()) < 0) {
          Field fieldTo = findField(toClass, key.toString());
          Field fieldFrom = findField(fromClass, key.toString());

          if (null == fieldTo) {
            logger.debug("fieldTo is null for class {}, using key {}", toClass,
                key);
          }
          
          if (null == fieldFrom) {
            logger.debug("fieldFrom is null for class {}, using key {}",
                fromClass, key);
          }

          if (null != fieldTo && null != fieldFrom) {
            fieldTo.setAccessible(true);
            fieldFrom.setAccessible(true);
            fieldTo.set(to, fieldFrom.get(from));
          }
        }
      } catch (Exception e) {
        logger.error(
            "Exception occured while setting value for key '{}' -  from ['{}'] ; to ['{}']:",
            key, from, to, e);
      }
    }
  }

  /**
   * Gets the keys from json.
   *
   * @param json
   *            the json
   * @return the keys from json
   * @throws Exception
   *             the exception
   */
  @SuppressWarnings("rawtypes")
  private List getKeysFromJson(final String json) throws Exception {
    Object things = gson.fromJson(json, Object.class);
    List keys = new ArrayList();
    collectAllTheKeys(keys, things);
    return keys;
  }

  /**
   * Collect all the keys.
   *
   * @param keys
   *            the keys
   * @param o
   *            the o
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void collectAllTheKeys(final List keys, final Object o) {
    Collection values = null;
    if (o instanceof Map) {
      Map map = (Map) o;
      keys.addAll(map.keySet());
      values = map.values();
    } else if (o instanceof Collection) {
      values = (Collection) o;
    } else {
      return;
    }

    for (Object value : values) {
      collectAllTheKeys(keys, value);
    }
  }
}
