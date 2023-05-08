package diplomka.diplomkaapiapp.entities.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import diplomka.diplomkaapiapp.entities.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String name;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String nameKz;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String descriptionKz;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String nameRu;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String descriptionRu;

    @NotNull
    @Column(columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.PRIVATE;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private User trainer;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private Collection<User> students = new ArrayList<>();

    public Course(String name, String description, String nameKz, String descriptionKz, String nameRu, String descriptionRu, User trainer) {
        this.name = name;
        this.description = description;
        this.nameKz = nameKz;
        this.descriptionKz = descriptionKz;
        this.nameRu = nameRu;
        this.descriptionRu = descriptionRu;
        this.trainer = trainer;
    }

    public void addStudent(User student) {
        this.students.add(student);
    }
}
