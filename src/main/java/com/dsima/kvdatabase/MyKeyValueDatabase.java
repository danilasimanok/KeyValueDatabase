package com.dsima.kvdatabase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

public class MyKeyValueDatabase implements KeyValueDatabase {
	public static final String DUMP_FILE_NAME = "key-value-database-dump";
	private HashMap<String, String> database;
	private PriorityQueue<StringAndDatePair> keysToRemove;
	private Integer defaultTtl;
	
	public MyKeyValueDatabase(Integer defaultTtl) {
		this.defaultTtl = defaultTtl;
		this.database = new HashMap<String, String>();
		this.keysToRemove = new PriorityQueue<StringAndDatePair>();
	}
	
	public MyKeyValueDatabase() {
		this(null);
	}
	
	private void purge() {
		Date current = new Date();
		StringAndDatePair pair = this.keysToRemove.peek();
		while ((pair != null) && (pair.date.before(current))) {
			this.keysToRemove.poll();
			this.database.remove(pair.string);
			pair = this.keysToRemove.peek();
		}
	}

	@Override
	public synchronized String get(String key) {
		this.purge();
		return this.database.get(key);
	}

	@Override
	public synchronized boolean set(String key, String value, Integer ttl) {
		this.purge();
		this.database.put(key, value);
		if (ttl == null)
			ttl = this.defaultTtl;
		if (ttl != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, ttl);
			Date whenToRemove = calendar.getTime();
			this.keysToRemove.add(new StringAndDatePair(whenToRemove, key));
		}
		return true;
	}

	@Override
	public synchronized String remove(String key) {
		this.purge();
		StringAndDatePair pairToRemove = null;
		for (StringAndDatePair pair: this.keysToRemove)
			if (pair.string.equals(key)) {
				pairToRemove = pair;
				break;
			}
		if (pairToRemove != null)
			this.keysToRemove.remove(pairToRemove);
		return this.database.remove(key);
	}

	@Override
	public synchronized void dump() {
		this.purge();
		try (ObjectOutputStream output =
				new ObjectOutputStream(new FileOutputStream(MyKeyValueDatabase.DUMP_FILE_NAME)))
		{
			output.writeObject(this.database);
			output.writeObject(this.keysToRemove);
			output.writeObject(this.defaultTtl);
		}
		catch (Exception e) {}
	}

	@Override
	public synchronized void load() {
		try (ObjectInputStream input =
				new ObjectInputStream(new FileInputStream(MyKeyValueDatabase.DUMP_FILE_NAME)))
		{
			this.database = (HashMap<String, String>) input.readObject();
			this.keysToRemove = (PriorityQueue<StringAndDatePair>) input.readObject();
			this.defaultTtl = (Integer) input.readObject();
		}
		catch (Exception e) {}
		this.purge();
	}
}