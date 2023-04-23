package diplomka.diplomkaapiapp.entities.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.Status;
import diplomka.diplomkaapiapp.request.QuestionnaireCreate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Double maxPoint;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<SectionBank> sectionBankList;

    public QuestionnaireBank(QuestionnaireCreate questionnaireCreate) {
        this.name = questionnaireCreate.getName();
        this.nameKz = questionnaireCreate.getNameKz();
        this.nameRu = questionnaireCreate.getNameRu();
    }

    @Transient
    private Double point;
    @Transient
    private Status status;
}
