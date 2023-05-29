package diplomka.diplomkaapiapp.config;

import diplomka.diplomkaapiapp.constants.EnvironmentProperties;
import diplomka.diplomkaapiapp.entities.chat.Chat;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.chat.ChatService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketChatConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final ChatService chatService;
    private final UserService userService;

    public WebSocketChatConfig(JwtService jwtService, ChatService chatService, UserService userService) {
        this.jwtService = jwtService;
        this.chatService = chatService;
        this.userService = userService;
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins(
                "http://localhost:3000",
                EnvironmentProperties.SERVER_URL,
                EnvironmentProperties.SERVER_URL_WWW
        ).withSockJS();

    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chat");
        registry.setApplicationDestinationPrefixes("/app");
    }



    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String chatId = accessor.getFirstNativeHeader("chatId");
                    String jwtToken = accessor.getFirstNativeHeader("Authorization");
                    Chat chat = chatService.getChatById(UUID.fromString(chatId));
                    if (chat == null) {
                        throw new IllegalArgumentException("Chat not found");
                    }
                    String username = jwtService.extractUsername(jwtToken);
                    User user = userService.getUserByUsername(username);

                    if (user == null) {
                        throw new IllegalArgumentException("User not found");
                    }

                    if (
                            chat.getUsers().stream().noneMatch(chatUser -> chatUser.getId().equals(user.getId()))
                    ) {
                        throw new IllegalArgumentException("User not registered in chat");
                    }
                }
                return message;
            }
        });
    }
}
