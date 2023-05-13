package diplomka.diplomkaapiapp.entities.postCourse;

import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.entities.course.CourseStatus;
import diplomka.diplomkaapiapp.entities.course.CourseUserStatus;
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
import java.util.stream.Collectors;

@Entity
@Table(name = "post_courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCourse {
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<User> students = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    private List<PostCourseSection> sections = new ArrayList<>();

    public void addSection(PostCourseSection section) {
        this.sections.add(section);
    }
    public void addStudent(User student) {
        this.students.add(student);
    }

    public void removeSectionById(UUID sectionId) {
        this.sections = this.sections.stream()
                .filter(postCourseSection -> !postCourseSection.getId().equals(sectionId))
                .collect(Collectors.toList());
    }


    public PostCourse(String name, String description, String nameKz, String descriptionKz, String nameRu, String descriptionRu) {
        this.name = name;
        this.description = description;
        this.nameKz = nameKz;
        this.descriptionKz = descriptionKz;
        this.nameRu = nameRu;
        this.descriptionRu = descriptionRu;
    }

    @Transient
    private CourseUserStatus userStatus = CourseUserStatus.IN_PROGRESS;
}
