package diplomka.diplomkaapiapp.controllers.chat;

import diplomka.diplomkaapiapp.entities.chat.Chat;
import diplomka.diplomkaapiapp.entities.chat.Message;
import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.ListPagination.ListPagination;
import diplomka.diplomkaapiapp.services.chat.ChatService;
import diplomka.diplomkaapiapp.services.chat.MessageService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat")
public class ChatController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;
    private final MessageService messageService;
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/get/my")
    public ResponseEntity getAllMyChats(@RequestHeader(value="Authorization") String token,
                                        @RequestParam("page") Integer page,
                                        @RequestParam("limit") Integer limit) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            List<Chat> chats = chatService.getAllChatsByUser(user, page, limit);
            List<Chat> nextChats = chatService.getAllChatsByUser(user, page + 1, limit);
            ListPagination<Chat> listPagination = new ListPagination<>(chats, !nextChats.isEmpty());

            listPagination.getList().forEach(chat -> {
                chat.setUser(
                        chat.getUsers().stream()
                                .filter(chatUser -> !chatUser.getId().equals(user.getId()))
                                .findFirst().orElse(null)
                );
            });

            return ResponseEntity.ok(listPagination);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity saveChat(@RequestParam("userID") UUID userID,
                                   @RequestHeader(value="Authorization") String token) {
        try {
            User user = userService.getUserById(userID);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            String username = jwtService.extractUsername(token);
            User userMe = userService.getUserByUsername(username);
            if (userMe == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            if (userMe.getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot save chat");
            }

            Chat existingChat = chatService.getChatBy2Users(user, userMe);
            if (existingChat != null) {
                existingChat.setUpdatedAt(LocalDateTime.now());
                existingChat = chatService.saveChat(existingChat);
                return ResponseEntity.ok(existingChat);
            }

            Chat chat = new Chat();
            chat.addUser(user);
            chat.addUser(userMe);

            chat = chatService.saveChat(chat);
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/send/message")
    public ResponseEntity sendMessageToChat(@PathVariable UUID id,
                                            @RequestParam("message") String messageStr,
                                            @RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            Chat chat = chatService.getChatById(id);
            if (chat == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat not found");
            }
            if (chat.getUsers().stream().noneMatch(chatUser -> chatUser.getId().equals(user.getId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user not registered in chat");
            }

            Message message = new Message(user, messageStr);
            message = messageService.saveMessage(message);
            chat.addMessage(message);
            chat.setUpdatedAt(LocalDateTime.now());
            chat = chatService.saveChat(chat);
            messagingTemplate.convertAndSend("/chat/" + chat.getId().toString(), message);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getChatById(@RequestHeader(value="Authorization") String token,
                                        @PathVariable UUID id) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            Chat chat = chatService.getChatById(id);
            if (chat == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat not found");
            }
            if (chat.getUsers().stream().noneMatch(chatUser -> chatUser.getId().equals(user.getId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user not registered in chat");
            }
            chat.setMessageList(chat.getMessages());
            chat.setUser(
                    chat.getUsers().stream()
                            .filter(chatUser -> !chatUser.getId().equals(user.getId()))
                            .findFirst().orElse(null)
            );

            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
