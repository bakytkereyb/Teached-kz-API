package diplomka.diplomkaapiapp.entities.competence.map;

import diplomka.diplomkaapiapp.entities.competence.bank.AnswerBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "answer_map")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerMap {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    @ManyToOne
    private QuestionBank questionBank;

    @ManyToOne
    private AnswerBank answerBank;

    private Boolean isSelected;
}
