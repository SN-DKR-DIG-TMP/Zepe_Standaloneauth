package sn.xadimousalih.springbootjwt.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import sn.xadimousalih.springbootjwt.models.UserStatus;
import sn.xadimousalih.springbootjwt.payload.request.LoginRequest;
import sn.xadimousalih.springbootjwt.payload.response.JwtResponse;
import sn.xadimousalih.springbootjwt.security.jwt.JwtUtils;
import sn.xadimousalih.springbootjwt.security.services.UserDetailsImpl;

@Service
public class AuthService {
	@Autowired
	private AccountService accountService;
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;

	public ResponseEntity<?> authenticateUser(@Valid LoginRequest loginRequest) {
		UserStatus status = accountService.getUserStatus(loginRequest.getUsername());
		if (status == null || status.equals(UserStatus.REMOVED)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		if (status.equals(UserStatus.BLOCKED)) {
			return ResponseEntity.status(HttpStatus.LOCKED).build();
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
				userDetails.getEmail(), userDetails.getStatus(), userDetails.isFirstConnection(), roles));
	}

}
