package com.dsima.kvdatabase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
class KvdatabaseApplicationTests {
	@Autowired
	private DatabaseController controller;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@LocalServerPort
	private int port;
	
	private static final String URL_TEMPLATE = "http://localhost:%d/%s";

	@Test
	void contextLoads() {
		assertThat(this.controller).isNotNull();
	}
	
	@Test
	void testPutItem() {
		String request = "item?key=k",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		Response<Boolean> expected = new Response<Boolean>(true, true),
				got = this.restTemplate.postForObject(url, "value", Response.class);
		assertThat(got).isEqualTo(expected);
	}
	
	@Test
	void testPutItemWithIncorrectTtl() {
		String request = "item?key=k&ttl=-4",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		Response<Boolean> expected = new Response<Boolean>(false, null),
				got = this.restTemplate.postForObject(url, "value", Response.class);
		assertThat(got).isEqualTo(expected);
	}
	
	@Test
	void testGetItem() {
		String request = "item?key=k",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		this.restTemplate.postForObject(url, "val", Response.class);
		Response<String> expected = new Response<String>(true, "val"),
				got = this.restTemplate.getForObject(url, Response.class);
		assertThat(got).isEqualTo(expected);
	}
	
	@Test
	void testGetNotExisingKey() {
		String request = "item?key=k",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		Response<String> expected = new Response<String>(false, null),
				got = this.restTemplate.getForObject(url, Response.class);
		assertThat(got).isEqualTo(expected);
	}
	
	@Test
	void testRemoveItem() {
		String request = "item?key=k",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		this.restTemplate.postForObject(url, "val", Response.class);
		Response<String> expected = new Response<String>(false, null);
		this.restTemplate.delete(url);
		Response<String> got = this.restTemplate.getForObject(url, Response.class);
		assertThat(got).isEqualTo(expected);
	}
	
	@Test
	void testTtlBeforeItRanOut() {
		String request = "item?key=k&ttl=4",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		this.restTemplate.postForObject(url, "val", Response.class);
		Response<String> expected = new Response<String>(true, "val"),
				got = this.restTemplate.getForObject(url, Response.class);
		assertThat(got).isEqualTo(expected);
	}
	
	@Test
	void testTtlAfterItRanOut() throws Throwable {
		String request = "item?key=k&ttl=1",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		this.restTemplate.postForObject(url, "val", Response.class);
		Thread.sleep(2000);
		Response<String> expected = new Response<String>(false, null),
				got = this.restTemplate.getForObject(url, Response.class);
		assertThat(got).isEqualTo(expected);
	}
	
	@Test
	void testDumpDownloaded() {
		String request = "dump",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		ResponseEntity<Resource> answer =
				this.restTemplate.getForObject(url, ResponseEntity.class);
		assertThat(answer.getStatusCode()).isEqualTo(HttpStatus.OK);
		
	}
	
	@Test
	void testLoadAfterDump() {
		String request = "dump",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		this.restTemplate.getForObject(url, ResponseEntity.class);
		request = "load";
		url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		Response<Boolean> expected = new Response<Boolean>(true, true),
				got = this.restTemplate.getForObject(url, Response.class);
		assertThat(got).isEqualTo(expected);
	}
	
	@Test
	void testLoadWithoutDump() {
		File dumpFile = new File(MyKeyValueDatabase.DUMP_FILE_NAME);
		dumpFile.delete();
		String request = "load",
				url = KvdatabaseApplicationTests.URL_TEMPLATE.formatted(this.port, request);
		Response<Boolean> expected = new Response<Boolean>(true, false),
				got = this.restTemplate.getForObject(url, Response.class);
		assertThat(got).isEqualTo(expected);
	}
}
