package com.mysite.doomchit.users;

import lombok.Getter;

// 값을 변경할 필요가 없으니 Setter 생략
@Getter
public enum UserRole {
	
	USER("ROLE_USER");
	
	private String value;
	
	UserRole(String value) {
		this.value = value;
	}
}
