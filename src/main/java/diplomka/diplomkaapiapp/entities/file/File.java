package diplomka.diplomkaapiapp.entities.file;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String fileName;

    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String label;

    public File(String fileName, String label) {
        this.fileName = fileName;
        this.label = label;
    }
}
