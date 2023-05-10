package diplomka.diplomkaapiapp.entities.course;

import com.fasterxml.jackson.annotation.JsonGetter;
import diplomka.diplomkaapiapp.entities.file.File;
import diplomka.diplomkaapiapp.entities.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "task_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    @NotNull
    private List<File> files;

    private Double grade;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Transient
    @JsonGetter
    public TaskStatus status() {
        if (grade != null) {
            return TaskStatus.GRADED;
        }
        if (files != null) {
            if (files.size() > 0) {
                return TaskStatus.SUBMITTED;
            }
        }
        return TaskStatus.NOT_SUBMITTED;
    }

    @ManyToOne
    @NotNull
    private User student;
}
