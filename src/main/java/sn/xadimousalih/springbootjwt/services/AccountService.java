package sn.xadimousalih.springbootjwt.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sn.xadimousalih.springbootjwt.models.ERole;
import sn.xadimousalih.springbootjwt.models.Role;
import sn.xadimousalih.springbootjwt.models.User;
import sn.xadimousalih.springbootjwt.models.UserStatus;
import sn.xadimousalih.springbootjwt.payload.request.ResetPasswordRequest;
import sn.xadimousalih.springbootjwt.payload.request.SignupRequest;
import sn.xadimousalih.springbootjwt.payload.request.UpdatePwdRequest;
import sn.xadimousalih.springbootjwt.payload.request.UpdateRoleRequest;
import sn.xadimousalih.springbootjwt.payload.response.AccountResponse;
import sn.xadimousalih.springbootjwt.payload.response.MessageResponse;
import sn.xadimousalih.springbootjwt.repository.RoleRepository;
import sn.xadimousalih.springbootjwt.repository.UserRepository;

import static sn.xadimousalih.springbootjwt.models.UserStatus.ACTIVATED;

@Service
@Transactional
public class AccountService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    public ResponseEntity<?> registerAll(@Valid List<SignupRequest> signUpRequest) {
        if (CollectionUtils.isEmpty(signUpRequest)) {
            return ResponseEntity.noContent().build();
        }
        signUpRequest.forEach(this::registerUser);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> registerUser(@Valid SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Ce username est déjà pris", "userIdExist"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: cette adresse mail est déjà utilisé!", "emailExist"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        user.setStatus(ACTIVATED);

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!", "userSuccess"));
    }

    public AccountResponse getAccountByUserId(String userId) {
        Optional<User> user = userRepository.findByUsername(userId);
        if (user.isPresent()) {
            User pUser = user.get();
            AccountResponse response = new AccountResponse();
            response.setEmail(pUser.getEmail());
            response.setUsername(pUser.getUsername());
            Set<Role> roles = pUser.getRoles();
            roles.forEach(role -> response.getRoles().add((role.getName().name())));
            response.setFirstConnection(pUser.isFirstConnection());
            response.setStatus(pUser.getStatus());
            return response;
        }
        return null;
    }

    public ResponseEntity<?> updateRole(@Valid UpdateRoleRequest updateRoleRequest) {
        if (updateRoleRequest == null || !userRepository.existsByUsername(updateRoleRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username does not exists !", "userIdNotExist"));
        }

        Set<Role> eRoles = getRoles(updateRoleRequest.getRole());
        User account = userRepository.findByUsername(updateRoleRequest.getUsername()).get();
        if (CollectionUtils.size(eRoles) != CollectionUtils.size(updateRoleRequest.getRole())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Roles don't exist !", "roleNotExist"));
        }
        account.setRoles(eRoles);
        userRepository.save(account);
        return ResponseEntity.ok().build();
    }

    private Set<Role> getRoles(Set<String> roles) {
        Set<Role> eRoles = new HashSet<>();
        for (String role : roles) {
            try {
                Optional<Role> er = roleRepository.findByName(convertRole(role));
                if (er.isPresent()) {
                    eRoles.add(er.get());
                }
            } catch (Exception e) {
                // si les listes n'ont pas la mm taille, exception levee plus haut
            }
        }
        return eRoles;
    }

    public UserStatus getUserStatus(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get().getStatus();
        }
        return null;
    }

    public ResponseEntity<?> deleteAccount(@Valid String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().setStatus(UserStatus.REMOVED);
            userRepository.save(user.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }
    
    public ResponseEntity<?> blockAccount(@Valid String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().setStatus(UserStatus.BLOCKED);
            userRepository.save(user.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> reactivateUser(@Valid ResetPasswordRequest resetPasswordRequest) {
        Optional<User> user = userRepository.findByUsername(resetPasswordRequest.getUserId());
        if (user.isPresent()) {
            User oldUser = user.get();
            oldUser.setFirstConnection(true);
            oldUser.setPassword(encoder.encode(resetPasswordRequest.getPwd()));
            oldUser.setStatus(ACTIVATED);
            userRepository.save(oldUser);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("User not exist!");
        }
    }

    public ResponseEntity<?> updatePassword(@Valid UpdatePwdRequest updatePwdRequest) {
        Optional<User> user = userRepository.findByUsername(updatePwdRequest.getUserId());
        if (user.isPresent()) {
            User oUser = user.get();
            if (encoder.matches(updatePwdRequest.getOldPwd(), oUser.getPassword())) {
                oUser.setPassword(encoder.encode(updatePwdRequest.getNewPwd()));
                oUser.setFirstConnection(false);
                userRepository.save(oUser);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Credentials not correct !");
            }
        }
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Credentials not correct !");
    }
    
    //Forgot Pwd
    public ResponseEntity<?> resetPassword(@Valid UpdatePwdRequest updatePwdRequest) {
        Optional<User> user = userRepository.findByUsername(updatePwdRequest.getUserId());
        if (user.isPresent()) {
            User oUser = user.get();
                oUser.setPassword(encoder.encode(updatePwdRequest.getNewPwd()));
                oUser.setFirstConnection(true);
                userRepository.save(oUser);
                return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Credentials not correct !");
    }

    private ERole convertRole(String role) {
        switch (role) {
            case "admin":
                return ERole.ROLE_ADMIN;
            case "mod":
                return ERole.ROLE_MODERATOR;
            default:
                return ERole.ROLE_USER;
        }
    }

}
