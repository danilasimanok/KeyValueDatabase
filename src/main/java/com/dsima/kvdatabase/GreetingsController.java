package com.dsima.kvdatabase;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingsController {
	@GetMapping("/greetings")
	public Greetings greetings(
			@RequestParam(value = "text", defaultValue = "Hello, World!")
			String text
	) {
		return new Greetings(text);
	}
}
