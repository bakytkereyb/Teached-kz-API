package diplomka.diplomkaapiapp.services.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.repositories.course.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeleteCourseService {
    private final CourseRepository courseRepository;

    public void deleteCourse(Course course) {
        course.setStudents(null);
        course.setSections(null);
        course.setClosedStudents(null);
        courseRepository.save(course);
        courseRepository.delete(course);
    }
}
