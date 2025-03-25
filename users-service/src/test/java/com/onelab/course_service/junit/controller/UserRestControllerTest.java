package com.onelab.course_service.junit.controller;

import com.onelab.users_service.controller.UserRestController;
import com.onelab.users_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.UserEditRequestDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Role;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.security.Principal;
import org.onelab.common.enums.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserRestController userRestController;

    @Test
    void getAllUsers_ShouldReturnUsersList() {

        UsersResponseDto userDto = new UsersResponseDto(1L, "test@example.com", "Test User", "Country", 25L, "ROLE_STUDENT", BigDecimal.valueOf(100), "USD");
        when(userService.getAllUsers(null)).thenReturn(List.of(userDto));

        ResponseEntity<List<UsersResponseDto>> response = userRestController.getAllUsers(null);

        assertEquals(1, response.getBody().size());
        assertEquals(userDto, response.getBody().get(0));
        verify(userService).getAllUsers(null);
    }

    @Test
    void searchUsers_ShouldReturnFilteredUsers() {
        UsersResponseDto userDto = new UsersResponseDto(1L, "test@example.com", "Test User", "USA", 25L, "ROLE_STUDENT", BigDecimal.valueOf(100), "USD");
        when(userService.searchUsers("Test", 20L, 30L, "USA", Role.ROLE_STUDENT, 0, 10))
                .thenReturn(List.of(userDto));

        ResponseEntity<List<UsersResponseDto>> response = userRestController.searchUsers(
                "Test", 20L, 30L, "USA", Role.ROLE_STUDENT, 0, 10);

        assertEquals(1, response.getBody().size());
        assertEquals(userDto, response.getBody().get(0));
        verify(userService).searchUsers("Test", 20L, 30L, "USA", Role.ROLE_STUDENT, 0, 10);
    }
    

    @Test
    void getUserInfo_ShouldReturnUserInfo() {
        UsersResponseDto userDto = new UsersResponseDto(1L, "user@example.com", "User", "Country", 30L, "ROLE_TEACHER", BigDecimal.valueOf(500), "USD");
        when(userService.getUserInfoById(1L)).thenReturn(userDto);

        ResponseEntity<UsersResponseDto> response = userRestController.getUserInfo(1L);

        assertEquals(userDto, response.getBody());
        verify(userService).getUserInfoById(1L);
    }

    @Test
    void getProfileInfo_ShouldReturnCurrentUserProfile() {
        UsersResponseDto profileDto = new UsersResponseDto(1L, "current@user.com", "Current User", "Country", 35L, "ADMIN", BigDecimal.valueOf(1000), "USD");
        when(principal.getName()).thenReturn("current@user.com");
        when(userService.getUserProfileByEmail("current@user.com")).thenReturn(profileDto);

        ResponseEntity<UsersResponseDto> response = userRestController.getProfileInfo(principal);

        assertEquals(profileDto, response.getBody());
        verify(userService).getUserProfileByEmail("current@user.com");
    }

    @Test
    void editProfile_ShouldUpdateAndReturnProfile() {
        UserEditRequestDto requestDto = new UserEditRequestDto("newPassword", "New Name", "New Country", 40L, Role.ROLE_TEACHER);
        UsersResponseDto updatedDto = new UsersResponseDto(1L, "user@example.com", "New Name", "New Country", 40L, "ROLE_TEACHER", BigDecimal.ZERO, "USD");
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.editProfile(requestDto, "user@example.com")).thenReturn(updatedDto);

        ResponseEntity<UsersResponseDto> response = userRestController.editProfile(requestDto, principal);

        assertEquals(updatedDto, response.getBody());
        verify(userService).editProfile(requestDto, "user@example.com");
    }

    @Test
    void deleteProfile_ShouldDeleteCurrentUser() {
        when(principal.getName()).thenReturn("user@delete.com");

        ResponseEntity<Void> response = userRestController.deleteProfile(principal);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService).deleteUserByEmail("user@delete.com");
    }

    @Test
    void replenishBalance_ShouldUpdateBalance() {
        UsersResponseDto updatedDto = new UsersResponseDto(1L, "user@example.com", "User", "Country", 30L, "ROLE_STUDENT", BigDecimal.valueOf(150), "USD");
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.replenishBalance("user@example.com", 50L, Currency.USD)).thenReturn(updatedDto);

        ResponseEntity<UsersResponseDto> response = userRestController.replenishBalance(50L, Currency.USD, principal);

        assertEquals(BigDecimal.valueOf(150), response.getBody().balance());
        verify(userService).replenishBalance("user@example.com", 50L, Currency.USD);
    }

    @Test
    void clearBalance_ShouldSetBalanceToZero() {
        UsersResponseDto clearedDto = new UsersResponseDto(1L, "user@example.com", "User", "Country", 30L, "ROLE_STUDENT", BigDecimal.ZERO, "USD");
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.clearBalance("user@example.com")).thenReturn(clearedDto);

        ResponseEntity<UsersResponseDto> response = userRestController.clearBalance(principal);

        assertEquals(BigDecimal.ZERO, response.getBody().balance());
        verify(userService).clearBalance("user@example.com");
    }
}