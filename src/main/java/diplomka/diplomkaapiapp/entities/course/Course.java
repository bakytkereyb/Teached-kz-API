package diplomka.diplomkaapiapp.entities.course;

import com.fasterxml.jackson.annotation.JsonGetter;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    private List<CourseSection> sections;

    public void addSection(CourseSection section) {
        this.sections.add(section);
    }
    public void removeSectionById(UUID sectionId) {
        this.sections = this.sections.stream()
                .filter(courseSection -> !courseSection.getId().equals(sectionId))
                .collect(Collectors.toList());
    }

    @ManyToOne
    private CourseSection certificateSection;

    @ManyToOne
    private User trainer;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<User> students = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private Collection<User> closedStudents = new ArrayList<>();

    @Transient
    private Integer progress;
    @Transient
    @JsonGetter
    public Integer maxProgress() {
        return sections == null ? 0 : sections.size();
    }

    @Transient
    private CourseUserStatus userStatus;



    public Course(String name, String description, String nameKz, String descriptionKz, String nameRu, String descriptionRu, User trainer, CourseSection certificateSection) {
        this.name = name;
        this.description = description;
        this.nameKz = nameKz;
        this.descriptionKz = descriptionKz;
        this.nameRu = nameRu;
        this.descriptionRu = descriptionRu;
        this.trainer = trainer;
        this.certificateSection = certificateSection;
    }

    public void addStudent(User student) {
        this.students.add(student);
    }
}
