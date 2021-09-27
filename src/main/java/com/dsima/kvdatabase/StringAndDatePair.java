package com.dsima.kvdatabase;

import java.io.Serializable;
import java.util.Date;

public class StringAndDatePair implements Serializable, Comparable<StringAndDatePair> {
	private static final long serialVersionUID = 1L;
	public Date date;
	public String string;
	
	public StringAndDatePair(Date date, String string) {
		this.date = date;
		this.string = string;
	}
	
	@Override
	public int compareTo(StringAndDatePair other) {
		return this.date.compareTo(other.date);
	}
}
