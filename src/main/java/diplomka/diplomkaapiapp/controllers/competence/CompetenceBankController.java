package diplomka.diplomkaapiapp.controllers.competence;

import diplomka.diplomkaapiapp.entities.competence.QuestionType;
import diplomka.diplomkaapiapp.entities.competence.Status;
import diplomka.diplomkaapiapp.entities.competence.bank.CompetenceBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import diplomka.diplomkaapiapp.entities.competence.map.AnswerMap;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.competence.bank.AnswerMapService;
import diplomka.diplomkaapiapp.services.competence.bank.CompetenceBankService;
import diplomka.diplomkaapiapp.services.competence.bank.ComponentBankService;
import diplomka.diplomkaapiapp.services.competence.bank.QuestionnaireBankService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/competence-bank")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Competence Bank", description = "APIs for operation on Competence Bank")
public class CompetenceBankController {
    private final CompetenceBankService competenceBankService;
    private final ComponentBankService componentBankService;
    private final QuestionnaireBankService questionnaireBankService;
    private final JwtService jwtService;
    private final UserService userService;
    private final AnswerMapService answerMapService;

    @GetMapping("/get")
    public ResponseEntity getCompetenceBank(@RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            CompetenceBank competenceBank = competenceBankService.getCompetenceBank();
            competenceBank.getComponentBankList().forEach(componentBank -> {
                Double[] componentAveragePoint = {0.0};
                componentBank.getQuestionnaireBankList().forEach(questionnaireBank -> {

                    if (questionnaireBankService.isUserPassedAnketa(questionnaireBank, user)) {
                        Double[] anketaPoint = {0.0};
                        questionnaireBank.setStatus(Status.FINISHED);
                        questionnaireBank.getSectionBankList().forEach(section -> {
                            Double[] pointSection = {0.0};
                            section.getQuestionBankList().forEach(question -> {
                                List<AnswerMap> userAnswers = answerMapService.getAllAnswersByQuestionAndUser(question, user);
                                question.setAnswerMapList(userAnswers);
                                Double questionPoint = getPointQuestion(question);
                                pointSection[0] += questionPoint;
                                question.setPoint(questionPoint);
                                if (user.isAdmin()) {
                                    question.getAnswerBankList().forEach(answerBank -> {
                                        answerBank.setIsCorrectPublic(answerBank.getIsCorrect());
                                    });
                                }
                            });
                            section.setPoint(pointSection[0]);
                            anketaPoint[0] += pointSection[0];
                        });

                        questionnaireBank.setPoint(anketaPoint[0]);
                        componentAveragePoint[0] += questionnaireBank.getPoint();
                    }
                });
                componentBank.setAveragePoint(componentAveragePoint[0]);
            });
            return ResponseEntity.ok(competenceBank);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/user/{id}")
    public ResponseEntity getCompetenceBankByUserId(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            CompetenceBank competenceBank = competenceBankService.getCompetenceBank();
            competenceBank.getComponentBankList().forEach(componentBank -> {
                Double[] componentAveragePoint = {0.0};
                componentBank.getQuestionnaireBankList().forEach(questionnaireBank -> {

                    if (questionnaireBankService.isUserPassedAnketa(questionnaireBank, user)) {
                        Double[] anketaPoint = {0.0};
                        questionnaireBank.setStatus(Status.FINISHED);
                        questionnaireBank.getSectionBankList().forEach(section -> {
                            Double[] pointSection = {0.0};
                            section.getQuestionBankList().forEach(question -> {
                                List<AnswerMap> userAnswers = answerMapService.getAllAnswersByQuestionAndUser(question, user);
                                question.setAnswerMapList(userAnswers);
                                Double questionPoint = getPointQuestion(question);
                                pointSection[0] += questionPoint;
                                question.setPoint(questionPoint);
                                if (user.isAdmin()) {
                                    question.getAnswerBankList().forEach(answerBank -> {
                                        answerBank.setIsCorrectPublic(answerBank.getIsCorrect());
                                    });
                                }
                            });
                            section.setPoint(pointSection[0]);
                            anketaPoint[0] += pointSection[0];
                        });

                        questionnaireBank.setPoint(anketaPoint[0]);
                        componentAveragePoint[0] += questionnaireBank.getPoint();
                    }
                });
                componentBank.setAveragePoint(componentAveragePoint[0]);
            });
            return ResponseEntity.ok(competenceBank);
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
}
