package diplomka.diplomkaapiapp.request.anketaCreate;

import lombok.Data;

@Data
public class AnswerCreate {
    private String answer;
    private String answerKz;
    private String answerRu;
    private Double point;
//    private Boolean isCorrect;
}
