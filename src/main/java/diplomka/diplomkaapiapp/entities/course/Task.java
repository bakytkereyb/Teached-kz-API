package diplomka.diplomkaapiapp.entities.course;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import diplomka.diplomkaapiapp.entities.file.File;
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
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private LocalDateTime deadline;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String name;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Task(LocalDateTime deadline, String name, String description) {
        this.deadline = deadline;
        this.name = name;
        this.description = description;
    }

    @Transient
    public boolean isEditable() {
        if (deadline == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(deadline);
    }

    @Transient
    public TaskFiles taskFiles;
}
