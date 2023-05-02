package diplomka.diplomkaapiapp.entities.competence.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import diplomka.diplomkaapiapp.entities.competence.bank.AnswerBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import diplomka.diplomkaapiapp.entities.user.User;
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
    @JsonIgnore
    private QuestionBank questionBank;

    @ManyToOne
    private AnswerBank answerBank;

    @ManyToOne
    @JsonIgnore
    private User user;

    private Boolean isSelected;

    public AnswerMap(String answerText, QuestionBank questionBank, AnswerBank answerBank, User user, Boolean isSelected) {
        this.answerText = answerText;
        this.questionBank = questionBank;
        this.answerBank = answerBank;
        this.user = user;
        this.isSelected = isSelected;
    }
}
