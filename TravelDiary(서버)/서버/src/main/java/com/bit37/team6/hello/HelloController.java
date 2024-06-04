package com.bit37.team6.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/")
	public String index() {
		return "메인 페이지 GET 응답";
	}
}
