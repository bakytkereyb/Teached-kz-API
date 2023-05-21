package diplomka.diplomkaapiapp.request.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CourseAnalytics {
    private UUID id;
    private String name;
    private String nameKz;
    private String nameRu;
    private String description;
    private String descriptionKz;
    private String descriptionRu;
    private int closedStudents;
    private int totalStudents;
}
