package diplomka.diplomkaapiapp.entities.competence.bank;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
}
