package diplomka.diplomkaapiapp.entities.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.QuestionType;
import diplomka.diplomkaapiapp.entities.competence.map.AnswerMap;
import diplomka.diplomkaapiapp.request.anketaCreate.QuestionCreate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "question_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String question;

    @NotNull
    @Column(columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private QuestionType type;

    private Double maxPoint;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AnswerBank> answerBankList;

    public QuestionBank(QuestionCreate questionCreate) {
        this.question = questionCreate.getQuestion();
        this.type = questionCreate.getType();
    }

    @Transient
    private List<AnswerMap> answerMapList;
    @Transient
    private Double point;
}
