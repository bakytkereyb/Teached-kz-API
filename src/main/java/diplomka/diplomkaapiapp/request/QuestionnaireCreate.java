package diplomka.diplomkaapiapp.request;

import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireCreate {
    private String name;
    private String nameKz;
    private String nameRu;
    private List<SectionCreate> sections;
}
