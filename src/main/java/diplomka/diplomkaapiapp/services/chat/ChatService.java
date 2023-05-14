package diplomka.diplomkaapiapp.services.chat;

import diplomka.diplomkaapiapp.entities.chat.Chat;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.repositories.chat.ChatRepository;
import diplomka.diplomkaapiapp.repositories.chat.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public Chat getChatById(UUID id) {
        return chatRepository.findById(id).orElse(null);
    }
    public Chat getChatBy2Users(User user1, User user2) {
        return chatRepository.findByUsersContainingAndUsersContaining(user1, user2);
    }

    public List<Chat> getAllChatsByUser(User user, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return chatRepository.findAllByUsersContainingOrderByUpdatedAtDesc(user, pageable);
    }
}
