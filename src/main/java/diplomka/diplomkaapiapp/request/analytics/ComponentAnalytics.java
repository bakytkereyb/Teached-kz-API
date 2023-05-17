package diplomka.diplomkaapiapp.request.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class ComponentAnalytics {
    private UUID id;
    private List<QuestionnaireAnalytics> questionnaires;
    private String name;
    private String nameKz;
    private String nameRu;
}
