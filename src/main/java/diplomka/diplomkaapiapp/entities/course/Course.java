package diplomka.diplomkaapiapp.entities.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.entities.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String name;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private User trainer;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private Collection<User> students = new ArrayList<>();

    public Course(String name, String description, User trainer) {
        this.name = name;
        this.description = description;
        this.trainer = trainer;
    }

    public void addStudent(User student) {
        this.students.add(student);
    }
}
