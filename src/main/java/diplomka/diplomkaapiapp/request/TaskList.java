package diplomka.diplomkaapiapp.request;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.entities.course.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class TaskList {
    private Task task;
    private Course course;
    private CourseSection section;
}
