package diplomka.diplomkaapiapp.controllers.competence;

import diplomka.diplomkaapiapp.entities.competence.QuestionType;
import diplomka.diplomkaapiapp.entities.competence.Status;
import diplomka.diplomkaapiapp.entities.competence.bank.*;
import diplomka.diplomkaapiapp.entities.competence.map.AnswerMap;
import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.anketaCreate.QuestionnaireCreate;
import diplomka.diplomkaapiapp.request.anketaPass.AnswerPass;
import diplomka.diplomkaapiapp.services.competence.bank.*;
import diplomka.diplomkaapiapp.services.course.CourseService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questionnaire-bank")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Questionnaire Bank", description = "APIs for operation on Questionnaire Bank")
public class QuestionnaireBankController {
    private final ComponentBankService componentBankService;
    private final QuestionnaireBankService questionnaireBankService;
    private final SectionBankService sectionBankService;
    private final QuestionBankService questionBankService;
    private final AnswerBankService answerBankService;
    private final AnswerMapService answerMapService;
    private final DeleteCompetenceBankService deleteCompetenceBankService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CourseService courseService;

    @PostMapping("/save")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = QuestionnaireBank.class))})
    })
    public ResponseEntity saveQuestionnaireBank(@RequestBody QuestionnaireCreate questionnaireCreate,
                                                @RequestParam("componentId")UUID componentId) {
        try {
            Course course = null;
            if (questionnaireCreate.getCourseId() != null) {
                course = courseService.getCourseById(questionnaireCreate.getCourseId());
                if (course == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
                }
            }

            ComponentBank componentBank = componentBankService.getComponentById(componentId);
            if (componentBank == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("component bank not found");
            }
            componentBank.setMaxPoint(componentBank.getMaxPoint() == null ? 0.0 : componentBank.getMaxPoint());
            List<SectionBank> sectionBankList = new ArrayList<>();

            QuestionnaireBank questionnaireBank = new QuestionnaireBank(questionnaireCreate);

            Double[] questionnaireMaxPoint = {0.0};

            questionnaireCreate.getSections().forEach(sectionCreate -> {
                SectionBank sectionBank = new SectionBank(sectionCreate);
                List<QuestionBank> questionBankList = new ArrayList<>();

                Double[] sectionMaxPoint = {0.0};

                sectionCreate.getQuestions().forEach(questionCreate -> {
                    QuestionBank questionBank = new QuestionBank(questionCreate);
                    List<AnswerBank> answerBankList = new ArrayList<>();

                    Double[] questionMaxPoint = {0.0};

                    questionCreate.getAnswers().forEach(answerCreate -> {
                        AnswerBank answerBank = new AnswerBank(answerCreate);
                        answerBank = answerBankService.saveAnswerBank(answerBank);
                        answerBankList.add(answerBank);

                        if (answerCreate.getIsCorrect() == null || answerCreate.getIsCorrect()) {
                            questionMaxPoint[0] = questionMaxPoint[0] + 1;
                        }
                    });

                    questionBank.setAnswerBankList(answerBankList);

                    questionBank.setMaxPoint(questionMaxPoint[0]);
                    if (questionBank.getType() != QuestionType.MCQ) {
                        questionBank.setMaxPoint(1.0);
                    }

                    questionBank = questionBankService.saveQuestionBank(questionBank);

                    questionBankList.add(questionBank);

                    sectionMaxPoint[0] = (sectionMaxPoint[0] + questionBank.getMaxPoint());
                });

                sectionBank.setQuestionBankList(questionBankList);
                sectionBank.setMaxPoint(sectionMaxPoint[0]);

                sectionBank = sectionBankService.saveSectionBank(sectionBank);

                sectionBankList.add(sectionBank);

                questionnaireMaxPoint[0] = questionnaireMaxPoint[0] + sectionBank.getMaxPoint();
            });

            questionnaireBank.setMaxPoint(questionnaireMaxPoint[0]);
            questionnaireBank.setSectionBankList(sectionBankList);
            questionnaireBank.setCourse(course);

            questionnaireBank = questionnaireBankService.saveQuestionnaireBank(questionnaireBank);

            componentBank.setMaxPoint(componentBank.getMaxPoint() + questionnaireBank.getMaxPoint());
            componentBank.addQuestionnaire(questionnaireBank);
            componentBankService.saveComponentBank(componentBank);

            return ResponseEntity.ok(questionnaireBank);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = QuestionnaireBank.class))})
    })
    public ResponseEntity getQuestionnaireById(@PathVariable UUID id,
                                               @RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            QuestionnaireBank questionnaireBank = questionnaireBankService.getQuestionnaireById(id);
            if (questionnaireBank == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("questionnaire bank not found");
            }
            questionnaireBank.setSections(questionnaireBank.getSectionBankList());

            if (questionnaireBankService.isUserPassedAnketa(questionnaireBank, user)) {
                Double[] anketaPoint = {0.0};
                questionnaireBank.setStatus(Status.FINISHED);
                questionnaireBank.getSections().forEach(section -> {
                    Double[] pointSection = {0.0};
                    section.getQuestionBankList().forEach(question -> {
                        List<AnswerMap> userAnswers = answerMapService.getAllAnswersByQuestionAndUser(question, user);
                        question.setAnswerMapList(userAnswers);
                        Double questionPoint = getPointQuestion(question);
                        pointSection[0] += questionPoint;
                        question.setPoint(questionPoint);
                    });
                    section.setPoint(pointSection[0]);
                    anketaPoint[0] += pointSection[0];
                });

                questionnaireBank.setPoint(anketaPoint[0]);
            }

            if (user.isAdmin()) {
                questionnaireBank.getSections().forEach(section -> {
                    section.getQuestionBankList().forEach(question -> {
                        question.getAnswerBankList().forEach(answerBank -> {
                            answerBank.setIsCorrectPublic(answerBank.getIsCorrect());
                        });
                    });
                });
            }

            return ResponseEntity.ok(questionnaireBank);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private Double getPointQuestion(QuestionBank questionBank) {
        Double[] point = {0.0};
        if (questionBank.getType() == QuestionType.OPEN) {
            List<AnswerMap> answerMapList = questionBank.getAnswerMapList().stream()
                    .filter(answerMap -> answerMap.getQuestionBank().getId() == questionBank.getId())
                    .collect(Collectors.toList());
            if (answerMapList.size() == 0) {
                return 0.0;
            }
            if (answerMapList.get(0).getIsSelected() == null) {
                return 0.0;
            }
            if (answerMapList.get(0).getIsSelected()) {
                return 1.0;
            } else {
                return 0.0;
            }
        }
        if (questionBank.getType() == QuestionType.LIST) {
            List<AnswerMap> answerMapList = questionBank.getAnswerMapList().stream()
                    .filter(answerMap -> answerMap.getQuestionBank().getId() == questionBank.getId())
                    .collect(Collectors.toList());
            if (answerMapList.size() == 0) {
                return 0.0;
            }
            for (AnswerMap answerMap : answerMapList) {
                if (answerMap.getIsSelected() != null) {
                    if (answerMap.getAnswerBank().getIsCorrect()) {
                        if (answerMap.getIsSelected()) {
                            point[0] += 1.0;
                        }
                    }
                }
            }
            if (point[0] > 0.0) {
                return 1.0;
            } else {
                return 0.0;
            }
        }
        if (questionBank.getType() == QuestionType.MCQ) {
            List<AnswerMap> answerMapList = questionBank.getAnswerMapList().stream()
                    .filter(answerMap -> answerMap.getQuestionBank().getId() == questionBank.getId())
                    .collect(Collectors.toList());
            if (answerMapList.size() == 0) {
                return 0.0;
            }
            Double divideNum = 1.0;
            for (AnswerMap answerMap : answerMapList) {
                if (answerMap.getIsSelected() != null) {
                    if (answerMap.getAnswerBank().getIsCorrect()) {
                        if (answerMap.getIsSelected()) {
                            point[0] += 1.0;
                        }
                    } else {
                        if (answerMap.getIsSelected()) {
                            divideNum += 1.0;
                        }
                    }
                }
            }

            point[0] /= divideNum;
        }
        return point[0];
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteQuestionnaireById(@PathVariable UUID id) {
        try {
            QuestionnaireBank questionnaireBank = questionnaireBankService.getQuestionnaireById(id);
            if (questionnaireBank == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("questionnaire bank not found");
            }
            deleteCompetenceBankService.deleteQuestionnaire(questionnaireBank);
            return ResponseEntity.ok(new String("questionnaire deleted"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/pass")
    public ResponseEntity passQuestionnaireById(
            @PathVariable UUID id,
            @RequestBody List<AnswerPass> userAnswers,
            @RequestHeader(value="Authorization") String token
    ) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            QuestionnaireBank questionnaireBank = questionnaireBankService.getQuestionnaireById(id);
            if (questionnaireBank == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("questionnaire bank not found");
            }

            if (questionnaireBank.getPassedUsers().stream().anyMatch(passedUser ->
                passedUser.getId().equals(user.getId())
            )) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("questionnaire has already been passed");
            }

            boolean[] isValidToSave = {true};
            List<AnswerMap> answersToSave = new ArrayList<>();

            userAnswers.forEach(userAnswer -> {
                QuestionBank questionBank = questionBankService.findQuestionById(userAnswer.getQuestionId());
                AnswerBank answerBank = answerBankService.findAnswerBankById(userAnswer.getAnswerId());
                if (questionBank == null || answerBank == null) {
                    isValidToSave[0] = false;
                } else {
                    AnswerMap answerMap = new AnswerMap(
                            userAnswer.getAnswerText(),
                            questionBank,
                            answerBank,
                            user,
                            userAnswer.getIsSelected()
                    );
                    answersToSave.add(answerMap);
                }
            });

            if (isValidToSave[0]) {
                answersToSave.forEach(answerMapService::saveAnswerMap);
                questionnaireBank.addPassedUser(user);
                questionnaireBankService.saveQuestionnaireBank(questionnaireBank);

                return ResponseEntity.ok(new String("questionnaire passed"));
            }
            return ResponseEntity.badRequest().body("Something went wrong");
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/get/component")
    public ResponseEntity getComponentBankByQuestionnaireId(@PathVariable UUID id) {
        try {
            QuestionnaireBank questionnaireBank = questionnaireBankService.getQuestionnaireById(id);
            if (questionnaireBank == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("questionnaire bank not found");
            }
            ComponentBank componentBank = componentBankService.getComponentBankByQuestionnaire(questionnaireBank);
            if (componentBank == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("component bank not found");
            }
            return ResponseEntity.ok(componentBank);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
