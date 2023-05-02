package diplomka.diplomkaapiapp.request.anketaPass;

import lombok.Data;

import java.util.UUID;

@Data
public class AnswerPass {
    private UUID questionId;
    private UUID answerId;
    private String answerText;
    private Boolean isSelected;
}
