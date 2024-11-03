package com.felix.healthcare.api_core.controller;

import com.felix.healthcare.api_core.dto.UsersDto;
import com.felix.healthcare.api_core.entity.Users;
import com.felix.healthcare.api_core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody UsersDto.SaveRequest data
            ) throws Exception {

        try {
            Users user = userService.save(data);

            UsersDto.SaveResponse response = new UsersDto.SaveResponse();
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRolesName(data.getRolesName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while saving user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving the user.");
        }

    }

}
