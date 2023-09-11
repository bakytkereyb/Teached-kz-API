package diplomka.diplomkaapiapp.entities.competence.bank;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import diplomka.diplomkaapiapp.entities.competence.Status;
import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.anketaCreate.QuestionnaireCreate;
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
@Table(name = "questionnaire_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireBank {
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
    private String nameKz;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String nameRu;

    @JsonGetter
    public Double maxPoint() {
        Double result = 0.0;
        for (SectionBank sectionBank : sectionBankList) {
            result += sectionBank.maxPoint();
        }
        return result;
    }

    @ManyToOne
    private Course course;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SectionBank> sectionBankList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> passedUsers = new ArrayList<>();

    public QuestionnaireBank(QuestionnaireCreate questionnaireCreate) {
        this.name = questionnaireCreate.getName();
        this.nameKz = questionnaireCreate.getNameKz();
        this.nameRu = questionnaireCreate.getNameRu();
    }

    @JsonIgnore
    public void addPassedUser(User user) {
        this.passedUsers.add(user);
    }

    @Transient
    private Double point;
    @Transient
    private Status status = Status.NO_START;
    @Transient
    private List<SectionBank> sections;
}
