package com.onelab.users_service.controller;

import com.onelab.users_service.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.MessageRequest;
import org.onelab.common.dto.response.ChatResponseDto;
import org.onelab.common.dto.response.ErrorResponseDto;
import org.onelab.common.dto.response.MessageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/chats")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "ChatController", description = "Endpoints for chat management")
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    @Operation(
            summary = "Start a chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource was created successfully"),
            @ApiResponse(responseCode = "403", description = "Authorization error", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<Map<String, String>> createChat(
            @RequestParam Long senderId,
            @RequestParam Long receiverId
    ) {
        String chatId = chatService.createChat(senderId, receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("chatId", chatId));
    }

    @GetMapping
    @Operation(
            summary = "Get all chats of current user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Authorization error", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<List<ChatResponseDto>> getChatsByReceiver(Principal principal) {
        return ResponseEntity.ok(chatService.getChatsByReceiverId(principal));
    }

    @PostMapping("/messages")
    @Operation(
            summary = "Send a message"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource was created successfully"),
            @ApiResponse(responseCode = "403", description = "Authorization error", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<Void> saveMessage(@RequestBody MessageRequest message) {
        chatService.saveMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/messages")
    @Operation(
            summary = "Set status 'SEEN' for chat's messages"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Request was accepted successfully"),
            @ApiResponse(responseCode = "403", description = "Authorization error", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<Void> setMessagesToSeen(@RequestParam String chatId,
                                                  Principal principal) {
        chatService.setMessagesToSeen(chatId, principal);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/{chatId}/messages")
    @Operation(
            summary = "Get messages by chatId",
            description = "Retrieves all messages of a chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Authorization error", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<List<MessageResponseDto>> getMessages(@PathVariable String chatId) {
        return ResponseEntity.ok(chatService.findChatMessages(chatId));
    }
}
