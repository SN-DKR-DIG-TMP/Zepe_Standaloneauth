package sn.xadimousalih.springbootjwt.payload.response;

import java.util.HashSet;
import java.util.Set;

import sn.xadimousalih.springbootjwt.models.UserStatus;

public class AccountResponse {
	private String username;
	private String email;
	private String password;
	private UserStatus status;
	private boolean isFirstConnection;
	private Set<String> roles = new HashSet<>();

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the roles
	 */
	public Set<String> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public boolean isFirstConnection() {
		return isFirstConnection;
	}

	public void setFirstConnection(boolean isFirstConnection) {
		this.isFirstConnection = isFirstConnection;
	}

}
