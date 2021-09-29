package com.dsima.kvdatabase;

public class Response<T> {
	
	private final boolean success;
	private final T payload;
	
	public Response(boolean success, T payload) {
		this.success = success;
		this.payload = payload;
	}
	
	public boolean getSuccess() {
		return this.success;
	}
	
	public T getPayload() {
		return this.payload;
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			Response<T> other = (Response<T>) o;
			return (this.success == other.success) &&
					(this.payload == other.payload || this.payload.equals(other.payload));
		} catch (ClassCastException e) {
			return false;
		}
	}
}
