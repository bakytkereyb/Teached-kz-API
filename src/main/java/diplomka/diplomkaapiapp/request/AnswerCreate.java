package diplomka.diplomkaapiapp.request;

import lombok.Data;

@Data
public class AnswerCreate {
    private String answer;
    private Boolean isCorrect;
}
