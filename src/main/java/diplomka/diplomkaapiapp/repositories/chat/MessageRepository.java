package diplomka.diplomkaapiapp.repositories.chat;

import diplomka.diplomkaapiapp.entities.chat.Chat;
import diplomka.diplomkaapiapp.entities.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID>, PagingAndSortingRepository<Message, UUID> {
}
