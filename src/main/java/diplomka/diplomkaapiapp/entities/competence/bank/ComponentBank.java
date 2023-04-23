package diplomka.diplomkaapiapp.entities.competence.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "component_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentBank {
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

    private Double maxPoint = 0.0;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<QuestionnaireBank> questionnaireBankList;

    public ComponentBank(String name, String nameKz, String nameRu) {
        this.name = name;
        this.nameKz = nameKz;
        this.nameRu = nameRu;
    }

    @JsonIgnore
    public void addQuestionnaire(QuestionnaireBank questionnaireBank) {
        this.questionnaireBankList.add(questionnaireBank);
    }

    @Transient
    private Double averagePoint;
}
