package diplomka.diplomkaapiapp.entities.competence.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private Double point;

//    @JsonIgnore
//    private Boolean isCorrect;
//
//    @Transient
//    private Boolean isCorrectPublic;

    public AnswerBank(AnswerCreate answerCreate) {
        this.answer = answerCreate.getAnswer();
//        this.isCorrect = answerCreate.getIsCorrect();
        this.point = answerCreate.getPoint();
    }
}
