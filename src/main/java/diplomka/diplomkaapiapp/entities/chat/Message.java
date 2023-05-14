package diplomka.diplomkaapiapp.entities.chat;

import diplomka.diplomkaapiapp.entities.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String message;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    public Message(User user, String message) {
        this.user = user;
        this.message = message;
    }
}
