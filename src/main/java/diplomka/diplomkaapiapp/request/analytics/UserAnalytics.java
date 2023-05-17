package diplomka.diplomkaapiapp.request.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserAnalytics {
    private long total;
    private long admins;
    private long trainers;
    private long users;
}
