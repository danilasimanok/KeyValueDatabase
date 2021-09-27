package com.dsima.kvdatabase;

public interface KeyValueDatabase {
	
	public String get(String key);
	
	public boolean set(String key, String value, Integer ttl);
	
	public String remove(String key);
	
	public void dump();
	
	public void load();
}