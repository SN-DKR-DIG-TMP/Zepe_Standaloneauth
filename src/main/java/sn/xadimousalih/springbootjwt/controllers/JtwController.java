package sn.xadimousalih.springbootjwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sn.xadimousalih.springbootjwt.security.jwt.JwtUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/jwt")
public class JtwController {

	@Autowired
	private JwtUtils jwtUtils;

	@GetMapping(value = "/validate/{token}")
	public ResponseEntity<Boolean> validateToken(@PathVariable("token") String token) {
		System.out.println(token);
		boolean result = jwtUtils.validateJwtToken(token);
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/userid/{token}")
	public ResponseEntity<String> getUserIdFromJwtToken(@PathVariable("token") String token) {
		return ResponseEntity.ok(jwtUtils.getUserNameFromJwtToken(token));
	}
}
