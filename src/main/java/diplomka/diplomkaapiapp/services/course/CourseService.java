package diplomka.diplomkaapiapp.services.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.CourseStatus;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.repositories.course.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course getCourseById(UUID id) {
        return courseRepository.findById(id).orElse(null);
    }

    public List<Course> getAllCourses(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return courseRepository.findAll(pageable).getContent();
    }

    public List<Course> getAllCourseWithoutPageable() {
        return courseRepository.findAll();
    }

    public List<Course> getAllCoursesByStatus(int page, int limit, CourseStatus status) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return courseRepository.findAllByStatus(status,pageable);
    }

    public List<Course> getAllCoursesByStatusWithoutPage(CourseStatus status) {
        return courseRepository.findAllByStatusWithoutPage(status);
    }

    public List<Course> getAllCoursesByStudent(User student, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return courseRepository.findAllByStudentsContaining(student, pageable);
    }

    public List<Course> getAllCoursesByTrainer(User trainer, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return courseRepository.findAllByTrainerOrderByNameAsc(trainer, pageable);
    }
}
