package diplomka.diplomkaapiapp.entities.application;

import diplomka.diplomkaapiapp.entities.course.CourseStatus;
import diplomka.diplomkaapiapp.entities.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.AWAITING;

    @Column(columnDefinition = "TEXT")
    private String title = "";

    @Column(columnDefinition = "TEXT")
    private String body = "";

    public Application(User user, String title, String body) {
        this.user = user;
        this.title = title;
        this.body = body;
    }
}
