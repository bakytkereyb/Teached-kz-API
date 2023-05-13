package diplomka.diplomkaapiapp.services.postCourse;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.CourseStatus;
import diplomka.diplomkaapiapp.entities.postCourse.PostCourse;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.repositories.postCourse.PostCourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class PostCourseService {
    private final PostCourseRepository postCourseRepository;

    public PostCourse savePostCourse(PostCourse postCourse) {
        return postCourseRepository.save(postCourse);
    }
    public PostCourse getPostCourseById(UUID id) {
        return postCourseRepository.findById(id).orElse(null);
    }

    public List<PostCourse> getAllPostCourses(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return postCourseRepository.findAll(pageable).getContent();
    }

    public List<PostCourse> getAllPostCoursesByStatus(int page, int limit, CourseStatus status) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return postCourseRepository.findAllByStatus(status,pageable);
    }

    public List<PostCourse> getAllPostCoursesByStudent(User student, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return postCourseRepository.findAllByStudentsContaining(student, pageable);
    }


}
