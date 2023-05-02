package diplomka.diplomkaapiapp.controllers.competence;

import diplomka.diplomkaapiapp.entities.competence.bank.*;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.QuestionnaireCreate;
import diplomka.diplomkaapiapp.services.competence.bank.*;
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
    private final DeleteCompetenceBankService deleteCompetenceBankService;

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
    public ResponseEntity getQuestionnaireById(@PathVariable UUID id) {
        try {
            QuestionnaireBank questionnaireBank = questionnaireBankService.getQuestionnaireById(id);
            if (questionnaireBank == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("questionnaire bank not found");
            }
            questionnaireBank.setSections(questionnaireBank.getSectionBankList());
            return ResponseEntity.ok(questionnaireBank);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
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
