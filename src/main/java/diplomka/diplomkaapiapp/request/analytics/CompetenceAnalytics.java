package diplomka.diplomkaapiapp.request.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
public class CompetenceAnalytics {
    private List<ComponentAnalytics> components;
}
