package diplomka.diplomkaapiapp.request.analytics;

import diplomka.diplomkaapiapp.entities.course.Course;
import lombok.Data;

import java.util.List;

@Data
public class CourseAnalytics {
    private long numberOfCourses;
    private List<Course> courses;
}
