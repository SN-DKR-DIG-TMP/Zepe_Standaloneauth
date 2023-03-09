package sn.xadimousalih.springbootjwt.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sn.xadimousalih.springbootjwt.payload.request.ResetPasswordRequest;
import sn.xadimousalih.springbootjwt.payload.request.SignupRequest;
import sn.xadimousalih.springbootjwt.payload.request.UpdatePwdRequest;
import sn.xadimousalih.springbootjwt.payload.request.UpdateRoleRequest;
import sn.xadimousalih.springbootjwt.payload.response.AccountResponse;
import sn.xadimousalih.springbootjwt.services.AccountService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/account")
public class AccountController {
	@Autowired
	AccountService accountService;

	@GetMapping("{uid}")
	public AccountResponse getAccountByUserId(@PathVariable("uid") String userId) {
		return accountService.getAccountByUserId(userId);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		return accountService.registerUser(signUpRequest);
	}

	@PostMapping("/registerall")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> registerAll(@Valid @RequestBody List<SignupRequest> signUpRequest) {
		return accountService.registerAll(signUpRequest);
	}

	@PutMapping("/updaterole")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateRole(@Valid @RequestBody UpdateRoleRequest updateRoleRequest) {
		return accountService.updateRole(updateRoleRequest);
	}

	@PutMapping("/updatepwd")
	public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePwdRequest updatePwdRequest) {
		return accountService.updatePassword(updatePwdRequest);
	}
	
	@PutMapping("/resetpwd")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody UpdatePwdRequest updatePwdRequest) {
		return accountService.resetPassword(updatePwdRequest);
	}

	@PutMapping("/reactivate-user")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> reactivateUser(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		return accountService.reactivateUser(resetPasswordRequest);
	}

	@DeleteMapping("/{uid}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteAccount(@Valid @PathVariable("uid") String userId) {
		return accountService.deleteAccount(userId);
	}
	
}
