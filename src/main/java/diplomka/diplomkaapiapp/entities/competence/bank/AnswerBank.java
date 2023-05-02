package diplomka.diplomkaapiapp.entities.competence.bank;

import diplomka.diplomkaapiapp.request.anketaCreate.AnswerCreate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "answer_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerBank {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String answer;

    private Boolean isCorrect;

    public AnswerBank(AnswerCreate answerCreate) {
        this.answer = answerCreate.getAnswer();
        this.isCorrect = answerCreate.getIsCorrect();
    }
}
