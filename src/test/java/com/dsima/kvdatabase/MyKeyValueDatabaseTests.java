package com.dsima.kvdatabase;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class MyKeyValueDatabaseTests {

	@Test
	void testSet() {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		assertTrue(database.set("example", "example", null));
	}
	
	@Test
	void testGet() {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		database.set("key1", "value1", null);
		database.set("key2", "value2", null);
		assertEquals(database.get("key1"), "value1");
		assertEquals(database.get("key2"), "value2");
	}
	
	@Test
	void testGetNotExisingKey() {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		assertNull(database.get("key"));
	}
	
	@Test
	void testRemove() {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		database.set("key", "value", null);
		assertEquals(database.get("key"), "value");
		assertEquals(database.remove("key"), "value");
		assertNull(database.get("key"));
	}
	
	@Test
	void testRemoveNotExistingKey() {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		database.set("key", "value", null);
		assertEquals(database.get("key"), "value");
		assertNull(database.remove("key1"));
		assertEquals(database.get("key"), "value");
	}
	
	@Test
	void testTtlBeforeItRanOut() throws Exception {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		database.set("key", "value", 1);
		Thread.sleep(100);
		assertEquals(database.get("key"), "value");
	}
	
	@Test
	void testTtlAfterItRanOut() throws Exception {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		database.set("key", "value", 1);
		Thread.sleep(2000);
		String result = database.get("key");
		assertNull(result);
	}
	
	@Test
	void testDumpAndLoad() {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		database.set("key1", "value1", null);
		database.set("key2", "value2", null);
		database.dump();
		database = new MyKeyValueDatabase();
		database.load();
		assertEquals(database.get("key1"), "value1");
		assertEquals(database.get("key2"), "value2");
	}
	
	@Test
	void testLoadWithoutDump() {
		MyKeyValueDatabase database = new MyKeyValueDatabase();
		File dumpFile = new File(MyKeyValueDatabase.DUMP_FILE_NAME);
		dumpFile.delete();
		assertFalse(database.load());
	}
}
