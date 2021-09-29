package com.dsima.kvdatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {
	@Autowired
	private MyKeyValueDatabase database;
	
	@GetMapping("/item")
	public Response<String> getItemByKey(@RequestParam(value = "key") String key) {
		String result = this.database.get(key);
		return new Response<String>(result != null, result);
	}
	
	@PostMapping("/item")
	public Response<Boolean> putItem(
			@RequestParam(value = "key") String key,
			@RequestParam(value = "ttl", defaultValue = "-1") Integer ttl,
			@RequestBody String value
	) {
		Boolean result;
		if (ttl == -1)
			result = this.database.set(key, value, null);
		else if (ttl < 0)
			result = null;
		else
			result = this.database.set(key, value, ttl);
		return new Response<Boolean>(result != null, result);
	}
	
	@DeleteMapping("/item")
	public Response<String> deleteItemByKey(@RequestParam(value = "key") String key) {
		String result = this.database.remove(key);
		return new Response<String>(result != null, result);
	}
	
	@GetMapping("/dump")
	public ResponseEntity<Resource> dump() {
		this.database.dump();
		InputStreamResource resource;
		try {
			resource = new InputStreamResource(
					new FileInputStream(MyKeyValueDatabase.DUMP_FILE_NAME)
			);
			return ResponseEntity.ok().
					contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
		} catch (FileNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/load")
	public Response<Boolean> load() {
		return new Response<Boolean>(true, this.database.load());
	}
}