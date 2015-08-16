package net.ljcomputing.gson.converter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.ljcomputing.gson.config.GsonConfiguration;
import net.ljcomputing.gson.converter.GsonConverterService;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.reflect.TypeToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GsonConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GsonConverterServiceImplTest {
    private static Logger logger = LoggerFactory
	    .getLogger(GsonConverterServiceImplTest.class);

    @Autowired
    private GsonConverterService gsonConverterService;

    public static Thing thing;

    public static Thing thingFrom;

    public static List<Thing> listOfThings = new ArrayList<Thing>();

    public static String json;

    @BeforeClass
    public static void setUp() {
	thing = new Thing();
	thing.setId(1L);
	thing.setKey("10");
	thing.setValue("100");
	thing.setUuid(UUID.randomUUID());

	thingFrom = new Thing();
	thingFrom.setId(100L);
	thingFrom.setKey("1000");
	thingFrom.setValue("10000");
	thingFrom.setUuid(thing.getUuid());

	for (int i = 0; i < 4; i++) {
	    Thing thingIndex = new Thing();
	    thingIndex.setId((long) i);
	    thingIndex.setKey("" + (i * 10));
	    thingIndex.setValue("" + (i * 100));
	    thingIndex.setUuid(UUID.randomUUID());
	    listOfThings.add(thingIndex);
	}
    }

    @Test
    public void test1ToJson() {
	json = gsonConverterService.toJson(thing);
	assertNotNull(json);
	logger.debug("json: {}", json);
    }

    @Test
    public void test2FromJsonStringClass() {
	Thing result = (Thing) gsonConverterService.fromJson(json, Thing.class);
	assertNotNull(result);
	logger.debug("result: {}", result);
    }

    @Test
    public void test3FromJsonStringType() {
	json = gsonConverterService.toJson(listOfThings);
	assertNotNull(json);
	logger.debug("json: {}", json);

	@SuppressWarnings("unchecked")
	List<Thing> result = gsonConverterService.fromJson(json,
		new TypeToken<List<Thing>>() {
		}.getType());

	assertNotNull(result);
	logger.debug("result: {}", result);
    }

    @Test
    public void test4Merge() throws Exception {
	thing = (Thing) gsonConverterService.merge(thing, thingFrom, new String[]{"uuid"});
	logger.debug("result: {}", thing);
	assertEquals(thingFrom, thing);
    }

}

class Thing {
    private UUID uuid;
    private Long id;
    private String key;
    private String value;

    public UUID getUuid() {
	return uuid;
    }

    public void setUuid(UUID uuid) {
	this.uuid = uuid;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getKey() {
	return key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    @Override
    public String toString() {
	return "Thing [uuid=" + uuid + ", id=" + id + ", key=" + key
		+ ", value=" + value + "]";
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}

	if (getClass() != obj.getClass()) {
	    return false;
	}

	final Thing other = (Thing) obj;
	return Objects.equals(this.id, other.id)
		&& Objects.equals(this.key, other.key)
		&& Objects.equals(this.value, other.value)
		&& Objects.equals(this.uuid, other.uuid);
    }

    @Override
    public int hashCode() {
	return Objects.hash(id, uuid, key, value);
    }

}