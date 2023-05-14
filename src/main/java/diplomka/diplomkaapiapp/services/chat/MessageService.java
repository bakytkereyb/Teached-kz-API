package diplomka.diplomkaapiapp.services.chat;

import diplomka.diplomkaapiapp.entities.chat.Message;
import diplomka.diplomkaapiapp.repositories.chat.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public Message getMessageById(UUID id) {
        return messageRepository.findById(id).orElse(null);
    }
}
