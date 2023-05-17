package diplomka.diplomkaapiapp.request.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class QuestionnaireAnalytics {
    private UUID id;
    private String name;
    private String nameKz;
    private String nameRu;
    private long numOfPassedUsers;
}
