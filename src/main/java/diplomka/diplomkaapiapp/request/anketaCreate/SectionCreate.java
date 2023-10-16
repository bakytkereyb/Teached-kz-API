package diplomka.diplomkaapiapp.request.anketaCreate;

import lombok.Data;

import java.util.List;

@Data
public class SectionCreate {
    private String name;
    private String nameKz;
    private String nameRu;
    private List<QuestionCreate> questions;
}
