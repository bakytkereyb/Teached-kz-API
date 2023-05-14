package diplomka.diplomkaapiapp.repositories.chat;

import diplomka.diplomkaapiapp.entities.chat.Chat;
import diplomka.diplomkaapiapp.entities.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID>, PagingAndSortingRepository<Chat, UUID> {
    List<Chat> findAllByUsersContainingOrderByUpdatedAtDesc(User user, Pageable pageable);
    Chat findByUsersContainingAndUsersContaining(User user1, User user2);
}
