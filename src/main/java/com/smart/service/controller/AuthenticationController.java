package com.smart.service.controller;


import com.smart.service.dtoRequest.AuthenticationRequest;
import com.smart.service.dtoRequest.RegisterUserRequest;
import com.smart.service.dtoRequest.RoleAssignRequest;
import com.smart.service.dtoResponse.APIsResponse;
import com.smart.service.exception.BadRequestException;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth-service")

public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public AuthenticationController(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<APIsResponse<?>> register(@RequestBody RegisterUserRequest registerDto) {
        // ✅ Check duplicate email → throw BadRequestException (400)
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BadRequestException("User with email already exists");
        }

        // ✅ Call service normally, no try/catch needed
        APIsResponse<?> response = authenticationService.register(registerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
    }

    @PostMapping("/assign-role")
    public ResponseEntity<APIsResponse<?>> assignRole(@RequestBody RoleAssignRequest request) {
        // ✅ service throws ResourceNotFoundException or BadRequestException as needed
        APIsResponse<?> response = authenticationService.assignRoleToUser(request);
        return ResponseEntity.ok(response); // 200 OK
    }


    @PostMapping("/authenticate")
    public ResponseEntity<APIsResponse<?>> authenticate(@RequestBody AuthenticationRequest request) {
        // ✅ service will throw UnauthorizedException for invalid credentials
        APIsResponse<?> response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response); // 200 OK
    }


}