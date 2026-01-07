package com.mysite.doomchit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class HelloController {
	
	@GetMapping("/hello")
	public String hello() {
		return "Hello World!";
	}
	
	@GetMapping("/jump")
	public String jump() {
		return "점프 투 스프링 부트";
	}

}
