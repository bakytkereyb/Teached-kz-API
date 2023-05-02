package diplomka.diplomkaapiapp.request.anketaCreate;

import diplomka.diplomkaapiapp.entities.competence.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class QuestionCreate {
    private String question;
    private QuestionType type; // MCQ , OPEN , LIST
    private List<AnswerCreate> answers;
}
