package com.onelab.users_service.controller;

import com.onelab.users_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.UserEditRequestDto;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "UsersController")
@Validated
@SecurityRequirement(name = "JWT")
public class UserRestController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UsersResponseDto>> getAllUsers(@RequestParam(required = false) Role role) {
        List<UsersResponseDto> responseDtoList = userService.getAllUsers(role);
        return ResponseEntity.ok(responseDtoList);
    }



    @GetMapping("/{userId}")
    @Operation(summary = "Get user info")
    public ResponseEntity<UsersResponseDto> getUserInfo(@PathVariable Long userId) {
        UsersResponseDto responseDto = userService.getUserInfoById(userId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get profile info")
    public ResponseEntity<UsersResponseDto> getProfileInfo(Principal principal) {
        UsersResponseDto responseDto = userService.getUserProfileByEmail(principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping
    @Operation(summary = "Edit profile")
    public ResponseEntity<UsersResponseDto> editProfile(@RequestBody @Valid UserEditRequestDto requestDto, Principal principal) {
        UsersResponseDto responseDto = userService.editProfile(requestDto, principal.getName());
        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping
    @Operation(summary = "Delete my profile")
    public ResponseEntity<Void> deleteProfile(Principal principal) {
        userService.deleteUserByEmail(principal.getName());
        return ResponseEntity.noContent().build();
    }


}
