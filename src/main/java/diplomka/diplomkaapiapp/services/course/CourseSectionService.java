package diplomka.diplomkaapiapp.services.course;

import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.repositories.course.CourseSectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseSectionService {
    private final CourseSectionRepository courseSectionRepository;

    public CourseSection saveCourseSection(CourseSection courseSection) {
        return courseSectionRepository.save(courseSection);
    }

    public CourseSection getCourseSectionById(UUID id) {
        return courseSectionRepository.findById(id).orElse(null);
    }
}
