package diplomka.diplomkaapiapp.request.anketaCreate;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuestionnaireCreate {
    private String name;
    private String nameKz;
    private String nameRu;
    private List<SectionCreate> sections;
    private UUID courseId;
}
