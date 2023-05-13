package diplomka.diplomkaapiapp.entities.postCourse;

import diplomka.diplomkaapiapp.entities.file.File;
import diplomka.diplomkaapiapp.entities.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "post_course_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String name;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    private List<File> files = new ArrayList<>();

    public void addFile(File file) {
        this.files.add(file);
    }

    public PostCourseSection(String name) {
        this.name = name;
    }

}
