package diplomka.diplomkaapiapp.services.postCourse;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.postCourse.PostCourse;
import diplomka.diplomkaapiapp.repositories.postCourse.PostCourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeletePostCourseService {
    private final PostCourseRepository postCourseRepository;

    public void deletePostCourse(PostCourse postCourse) {
        postCourse.setStudents(null);
        postCourse.setSections(null);
        postCourseRepository.save(postCourse);
        postCourseRepository.delete(postCourse);
    }
}
