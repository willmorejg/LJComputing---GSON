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

import net.ljcomputing.gson.converter.GsonConverterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

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

    /**
     * @see net.ljcomputing.gson.converter.GsonConverterService#toJson(java.lang.Object)
     */
    public final String toJson(final Object source) {
	return new Gson().toJson(source);
    }

    /**
     * @see net.ljcomputing.gson.converter.GsonConverterService#fromJson(java.lang.String,
     *      java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public final Object fromJson(final String json,
	    @SuppressWarnings("rawtypes") final Class target) {
	return new Gson().fromJson(json, target);
    }

    /**
     * @see net.ljcomputing.gson.converter.GsonConverterService#fromJson(java.lang.String,
     *      java.lang.reflect.Type)
     */
    @SuppressWarnings("rawtypes")
    public final List fromJson(final String json, final Type target) {
	return new Gson().fromJson(json, target);
    }

    /**
     * @see net.ljcomputing.gson.converter.GsonConverterService#merge(java.lang.Object,
     *      java.lang.Object)
     */
    public final Object merge(final Object to, final Object from) {
	return merge(to, from, null);
    }

    /**
     * @see net.ljcomputing.gson.converter.GsonConverterService#merge(java.lang.Object,
     *      java.lang.Object, java.lang.String[])
     */
    public final Object merge(final Object to, final Object from,
	    final String[] ignoredProperties) {
	try {
	    mergeValues(getKeysFromJson(toJson(from)), to, from,
		    ignoredProperties);
	} catch (Exception e) {
	    logger.error("Exception occured while merging {} with {}", from,
		    to, e);
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
		logger.debug("exception ignored: {}", e.getMessage());
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
			&& Arrays.binarySearch(ignoredProperties,
				key.toString()) < 0) {
		    Field fieldTo = findField(toClass, key.toString());
		    fieldTo.setAccessible(true);
		    Field fieldFrom = findField(fromClass, key.toString());
		    fieldFrom.setAccessible(true);
		    fieldTo.set(to, fieldFrom.get(from));
		}
	    } catch (Exception e) {
		logger.error(
			"Exception occured while setting values from {} to {}:",
			from, to, e);
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
	Object things = new Gson().fromJson(json, Object.class);
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
