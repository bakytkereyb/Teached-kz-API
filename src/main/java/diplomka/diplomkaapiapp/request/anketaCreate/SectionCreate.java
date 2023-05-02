package diplomka.diplomkaapiapp.request.anketaCreate;

import lombok.Data;

import java.util.List;

@Data
public class SectionCreate {
    private String name;
    private List<QuestionCreate> questions;
}
