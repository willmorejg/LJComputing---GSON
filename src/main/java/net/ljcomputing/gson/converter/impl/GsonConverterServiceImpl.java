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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.ljcomputing.gson.converter.GsonConverterService;
import net.ljcomputing.gson.strategy.ExcludeFromJsonAnnotationExclusionStrategy;

/**
 * GSON converter service implementation.
 * 
 * @author James G. Willmore
 *
 */
@Service
public class GsonConverterServiceImpl implements GsonConverterService {
  /** The SLF4J Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(GsonConverterServiceImpl.class);

  /** The Gson instance. */
  private transient final Gson gson;

  /**
   * Instantiates a new gson converter service impl.
   */
  public GsonConverterServiceImpl() {
    gson = new GsonBuilder()
        .setExclusionStrategies(new ExcludeFromJsonAnnotationExclusionStrategy()).serializeNulls()
        .create();
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
  public final Object fromJson(final String json, final Class<?> target) {
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
    Object result = null;
    final Class<?> toClass = to.getClass();
    final Class<?> fromClass = from.getClass();

    if (toClass.equals(fromClass)) {
      result = merge(to, from, new String[] {});
    }

    return result;
  }

  /**
   * @see net.ljcomputing.gson.converter.GsonConverterService
   * #merge(java.lang.Object, java.lang.Object, java.lang.String[])
   */
  public final Object merge(final Object to, final Object from, final String... ignoredProperties) {
    mergeValues(getKeysFromJson(toJson(from)), to, from, ignoredProperties);
    return to;
  }

  /**
   * Find field.
   *
   * @param clazz the clazz
   * @param fieldName the field name
   * @return the field
   */
  private Field findField(final Class<?> clazz, final String fieldName) {
    Field field = null;
    Class<?> current = clazz;

    do {
      try {
        field = current.getDeclaredField(fieldName);
      } catch (NoSuchFieldException | SecurityException exception) {
        LOGGER.debug("exception ignored while getting declared field {} for class {}: {}",
            fieldName, clazz, exception.getMessage());
      }
    } while ((current = current.getSuperclass()) != null);

    return field;
  }

  /**
   * Merge values.
   *
   * @param keysFrom the keys from
   * @param to the to Object
   * @param from the from Object
   * @param ignoredProperties the ignored properties
   */
  @SuppressWarnings("rawtypes")
  private void mergeValues(final List keysFrom, final Object to, final Object from,
      final String... ignoredProperties) {
    final Class toClass = to.getClass();
    final Class fromClass = from.getClass();

    if (null != ignoredProperties) {
      Arrays.sort(ignoredProperties);
    }

    for (final Object key : keysFrom) {
      try {
        if (null != ignoredProperties
            && Arrays.binarySearch(ignoredProperties, key.toString()) < 0) {
          final Field fieldTo = findField(toClass, key.toString());
          final Field fieldFrom = findField(fromClass, key.toString());

          if (null == fieldTo) {
            LOGGER.debug("fieldTo is null for class {}, using key {}", toClass, key);
          }

          if (null == fieldFrom) {
            LOGGER.debug("fieldFrom is null for class {}, using key {}", fromClass, key);
          }

          setFieldValues(to, fieldTo, from, fieldFrom);
        }
      } catch (IllegalArgumentException | IllegalAccessException exception) {
        LOGGER.error(
            "Exception occured while setting value for key '{}' -  from ['{}'] ; to ['{}']:", key,
            from, to, exception);
      }
    }
  }
  
  /**
   * Sets the field values.
   *
   * @param to the to
   * @param fieldTo the field to
   * @param from the from
   * @param fieldFrom the field from
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  private static void setFieldValues(final Object to, final Field fieldTo, final Object from,
      final Field fieldFrom) throws IllegalArgumentException, IllegalAccessException {
    if (null != fieldTo && null != fieldFrom) {
      fieldTo.setAccessible(true);
      fieldFrom.setAccessible(true);
      fieldTo.set(to, fieldFrom.get(from));
    }
  }

  /**
   * Gets the keys from json.
   *
   * @param json the json
   * @return the keys from json
   */
  @SuppressWarnings("rawtypes")
  private List getKeysFromJson(final String json) {
    final Object things = gson.fromJson(json, Object.class);
    final List keys = new ArrayList();
    collectAllTheKeys(keys, things);
    return keys;
  }

  /**
   * Collect all the keys.
   *
   * @param keys the keys
   * @param object the object
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void collectAllTheKeys(final List keys, final Object object) {
    Collection values = null;

    if (object instanceof Map) {
      final Map map = (Map) object;
      keys.addAll(map.keySet());
      values = map.values();
    } else if (object instanceof Collection) {
      values = (Collection) object;
    } else {
      return;
    }

    for (final Object value : values) {
      collectAllTheKeys(keys, value);
    }
  }
}
