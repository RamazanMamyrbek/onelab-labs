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

    @GetMapping("/search")
    @Operation(summary = "Search users")
    public ResponseEntity<List<UsersResponseDto>> searchUsers(
            @RequestParam(value = "nameQuery", required = false) String nameQuery,
            @RequestParam(value = "minAge", required = false, defaultValue = "7") Long minAge,
            @RequestParam(value = "maxAge", required = false) Long maxAge,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "role", required = false) Role role,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<UsersResponseDto> usersResponseDtoList = userService.searchUsers(nameQuery, minAge, maxAge, country, role, page, size);
        return ResponseEntity.ok(usersResponseDtoList);
    }

    @GetMapping("/courses/{courseId}/students")
    @Operation(summary = "Get students for course")
    ResponseEntity<List<UsersResponseDto>> getStudentsForCourse(@PathVariable Long courseId) {
        List<UsersResponseDto> responseDtoList = userService.getStudentsForCourse(courseId);
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
