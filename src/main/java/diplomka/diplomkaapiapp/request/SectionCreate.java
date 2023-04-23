package diplomka.diplomkaapiapp.request;

import lombok.Data;

import java.util.List;

@Data
public class SectionCreate {
    private String name;
    private List<QuestionCreate> questions;
}
