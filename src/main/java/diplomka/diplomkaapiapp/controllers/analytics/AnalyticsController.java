package diplomka.diplomkaapiapp.controllers.analytics;

import diplomka.diplomkaapiapp.entities.competence.bank.CompetenceBank;
import diplomka.diplomkaapiapp.entities.competence.bank.ComponentBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionnaireBank;
import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.request.analytics.*;
import diplomka.diplomkaapiapp.services.competence.bank.AnswerMapService;
import diplomka.diplomkaapiapp.services.competence.bank.CompetenceBankService;
import diplomka.diplomkaapiapp.services.competence.bank.ComponentBankService;
import diplomka.diplomkaapiapp.services.competence.bank.QuestionnaireBankService;
import diplomka.diplomkaapiapp.services.course.CourseService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.postCourse.PostCourseService;
import diplomka.diplomkaapiapp.services.user.RoleService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "APIs for operation on Analytics")
public class AnalyticsController {
    private final CompetenceBankService competenceBankService;
    private final ComponentBankService componentBankService;
    private final QuestionnaireBankService questionnaireBankService;
    private final JwtService jwtService;
    private final UserService userService;
    private final RoleService roleService;
    private final AnswerMapService answerMapService;
    private final CourseService courseService;
    private final PostCourseService postCourseService;

    @GetMapping("/users")
    public ResponseEntity getNumberOfUsers() {
        try {
            long total = userService.countAllUsers();
            Role admin = roleService.getRoleByName("admin");
            Role trainer = roleService.getRoleByName("trainer");
            Role user = roleService.getRoleByName("user");

            long admins = userService.countUsersByRole(admin);
            long trainers = userService.countUsersByRole(trainer);
            long users = userService.countUsersByRole(user);

            UserAnalytics userAnalytics = new UserAnalytics(total, admins, trainers, users);

            return ResponseEntity.ok(userAnalytics);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/competence-bank")
    public ResponseEntity getAnalyticsOfCompetenceBank() {
        try {
            CompetenceBank competenceBank = competenceBankService.getCompetenceBank();
            List<ComponentAnalytics> componentAnalyticsList = new ArrayList<>();
            for (ComponentBank component : competenceBank.getComponentBankList()) {
                List<QuestionnaireAnalytics> questionnaires = new ArrayList<>();
                for (QuestionnaireBank questionnaire : component.getQuestionnaireBankList()) {
                    QuestionnaireAnalytics questionnaireAnalytics = new QuestionnaireAnalytics(
                            questionnaire.getId(),
                            questionnaire.getName(),
                            questionnaire.getNameKz(),
                            questionnaire.getNameRu(),
                            questionnaire.getPassedUsers().size()
                    );
                    questionnaires.add(questionnaireAnalytics);
                }
                ComponentAnalytics componentAnalytics = new ComponentAnalytics(
                        component.getId(),
                        questionnaires,
                        component.getName(),
                        component.getNameKz(),
                        component.getNameRu()
                );
                componentAnalyticsList.add(componentAnalytics);
            }

            CompetenceAnalytics competenceAnalytics = new CompetenceAnalytics(componentAnalyticsList);
            return ResponseEntity.ok(competenceAnalytics);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }

    @GetMapping("/course/{id}")
    public ResponseEntity getAnalyticsOfCourseById(@PathVariable UUID id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            CourseAnalytics courseAnalytics = new CourseAnalytics(
                    course.getId(),
                    course.getName(),
                    course.getNameKz(),
                    course.getNameRu(),
                    course.getDescription(),
                    course.getDescriptionKz(),
                    course.getDescriptionRu(),
                    course.getClosedStudents().size(),
                    course.getStudents().size()
            );
            return ResponseEntity.ok(courseAnalytics);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
