package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.*;
import diplomka.diplomkaapiapp.entities.competence.map.AnswerMap;
import diplomka.diplomkaapiapp.repositories.competence.bank.*;
import diplomka.diplomkaapiapp.repositories.competence.map.AnswerMapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeleteCompetenceBankService {

    private final CompetenceBankRepository competenceBankRepository;
    private final QuestionnaireBankRepository questionnaireBankRepository;
    private final ComponentBankRepository componentBankRepository;
    private final SectionBankRepository sectionBankRepository;
    private final QuestionBankRepository questionBankRepository;
    private final AnswerBankRepository answerBankRepository;
    private final AnswerMapRepository answerMapRepository;

    public void deleteComponent(ComponentBank componentBank) {
        CompetenceBank competenceBank = competenceBankRepository.findByName("Competence Bank");
        if (competenceBank != null) {
            competenceBank.setComponentBankList(
                    competenceBank.getComponentBankList().stream()
                            .filter(component -> component.getId() != componentBank.getId())
                            .collect(Collectors.toList())
            );
            competenceBankRepository.save(competenceBank);
        }
        List<QuestionnaireBank> questionnaireBankList = componentBank.getQuestionnaireBankList();
        componentBank.setQuestionnaireBankList(null);
        componentBankRepository.save(componentBank);
        componentBankRepository.delete(componentBank);

        for (QuestionnaireBank questionnaireBank : questionnaireBankList) {
            deleteQuestionnaire(questionnaireBank);
        }
    }

    public void deleteQuestionnaire(QuestionnaireBank questionnaireBank){
        ComponentBank componentBank = componentBankRepository.findByQuestionnaireBankListContaining(questionnaireBank);
        if (componentBank != null) {
            componentBank.setQuestionnaireBankList(
                    componentBank.getQuestionnaireBankList().stream()
                            .filter(anketa -> anketa.getId() != questionnaireBank.getId())
                            .collect(Collectors.toList())
            );
            componentBank.setMaxPoint(componentBank.getMaxPoint() - questionnaireBank.getMaxPoint());
            componentBankRepository.save(componentBank);
        }
        List<SectionBank> sectionBankList = questionnaireBank.getSectionBankList();
        questionnaireBank.setSections(null);
        questionnaireBank.setPassedUsers(null);
        questionnaireBankRepository.save(questionnaireBank);
        questionnaireBankRepository.delete(questionnaireBank);

        for (SectionBank sectionBank : sectionBankList) {
            deleteSection(sectionBank);
        }
    }

    public void deleteSection(SectionBank sectionBank) {
        QuestionnaireBank questionnaireBank = questionnaireBankRepository.findBySectionBankListContaining(sectionBank);
        if (questionnaireBank != null) {
            questionnaireBank.setSectionBankList(
                    questionnaireBank.getSections().stream()
                            .filter(section -> section.getId() != sectionBank.getId())
                            .collect(Collectors.toList())
            );
            questionnaireBankRepository.save(questionnaireBank);
        }
        List<QuestionBank> questionBankList = sectionBank.getQuestionBankList();
        sectionBank.setQuestionBankList(null);
        sectionBankRepository.save(sectionBank);
        sectionBankRepository.delete(sectionBank);

        for (QuestionBank questionBank : questionBankList) {
            deleteQuestion(questionBank);
        }
    }

    public void deleteQuestion(QuestionBank questionBank) {
        SectionBank sectionBank = sectionBankRepository.findByQuestionBankListContaining(questionBank);
        if (sectionBank != null) {
            sectionBank.setQuestionBankList(
                    sectionBank.getQuestionBankList().stream()
                            .filter(question -> question.getId() != questionBank.getId())
                            .collect(Collectors.toList())
            );
            sectionBankRepository.save(sectionBank);
        }

        List<AnswerBank> answerBankList = questionBank.getAnswerBankList();
        questionBank.setAnswerBankList(null);
        questionBankRepository.save(questionBank);
        questionBankRepository.delete(questionBank);

        for (AnswerBank answerBank : answerBankList) {
            deleteAnswer(answerBank);
        }
    }

    public void deleteAnswer(AnswerBank answerBank) {
        QuestionBank questionBank = questionBankRepository.findByAnswerBankListContaining(answerBank);
        if (questionBank != null) {
            questionBank.setAnswerBankList(
                    questionBank.getAnswerBankList().stream()
                            .filter(answer -> answer.getId() != answerBank.getId())
                            .collect(Collectors.toList())
            );
            questionBankRepository.save(questionBank);
        }
        answerBankRepository.delete(answerBank);
    }
}
