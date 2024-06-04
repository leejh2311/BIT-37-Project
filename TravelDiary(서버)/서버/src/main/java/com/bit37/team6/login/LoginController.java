package com.bit37.team6.login;

import com.bit37.team6.register.AccountEntity;
import com.bit37.team6.register.AccountRepository;
import com.bit37.team6.response.JSONResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class LoginController {
	@Autowired
	AccountRepository accountRepository;

	@GetMapping("/login")
	public String hello() {
		return "로그인 페이지 GET 응답";
	}
	
	@PostMapping("/login")
	public JSONResponse<LoginDTO> login(@RequestBody LoginDTO loginDTO) {
		String msg;
		int code;

		System.out.print("[Login] ");
		System.out.print("ID: " + loginDTO.getUserId() + "\t");
		System.out.println("PW: " + loginDTO.getUserPw());

		AccountEntity accountEntity = accountRepository.findByUserId(loginDTO.getUserId());
		if(accountEntity == null){
			code = 0;
			msg = "ID does not exist";
		}
		else{
			if(Objects.equals(accountEntity.getUserPw(), loginDTO.getUserPw())) {
				code = 1;
				msg = "OK";
			} else {
				code = 0;
				msg = "Wrong password";
			}
		}

		return new JSONResponse<LoginDTO>(code, msg, loginDTO);
	}
}
