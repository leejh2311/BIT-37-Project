package com.bit37.team6.register;

import com.bit37.team6.response.JSONResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {
	@Autowired
	AccountRepository accountRepository;

	@GetMapping("/register")
	public String hello() {
		return "회원가입 페이지 GET 응답";
	}

	//RequestBody로 받으면 Json 데이터를 자바 객체로 변환 가능
	@Transactional
	@PostMapping("/register")
	public JSONResponse<RegisterDTO> register(@RequestBody RegisterDTO registerDTO) {
		int code;
		String msg;

		System.out.print("[Register] ");
		System.out.print("ID: " + registerDTO.getUserId() + "\t");
		System.out.println("PW: " + registerDTO.getUserPw());

		AccountEntity newAccount = new AccountEntity();
		newAccount.setUserId(registerDTO.getUserId());
		newAccount.setUserPw(registerDTO.getUserPw());

		if(accountRepository.findByUserId(newAccount.getUserId()) != null){
			code = 0;
			msg = "ID already exists";
		} else {
			code = 1;
			msg = "OK";
			accountRepository.save(newAccount);
		}

		return new JSONResponse<RegisterDTO>(code, msg, registerDTO);
	}
}
