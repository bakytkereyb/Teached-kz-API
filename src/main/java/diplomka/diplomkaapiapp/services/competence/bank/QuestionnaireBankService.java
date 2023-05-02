package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.*;
import diplomka.diplomkaapiapp.repositories.competence.bank.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionnaireBankService {
    private final QuestionnaireBankRepository questionnaireBankRepository;
    private final ComponentBankRepository componentBankRepository;
    private final SectionBankRepository sectionBankRepository;
    private final QuestionBankRepository questionBankRepository;
    private final AnswerBankRepository answerBankRepository;

    public QuestionnaireBank saveQuestionnaireBank(QuestionnaireBank questionnaireBank) {
        return questionnaireBankRepository.save(questionnaireBank);
    }

    public QuestionnaireBank getQuestionnaireById(UUID id) {
        return questionnaireBankRepository.findById(id).orElse(null);
    }

    public void deleteQuestionnaire(QuestionnaireBank questionnaireBank) {
        ComponentBank componentBank = componentBankRepository.findByQuestionnaireBankListContaining(questionnaireBank);
        if (componentBank != null) {
            componentBank.setQuestionnaireBankList(
                    componentBank.getQuestionnaireBankList().stream()
                            .filter(anketa -> anketa.getId() != questionnaireBank.getId())
                            .collect(Collectors.toList())
            );
            System.out.println(componentBank.getQuestionnaireBankList().toString());
            componentBankRepository.save(componentBank);
        }
        List<SectionBank> sectionBankList = questionnaireBank.getSectionBankList();
        questionnaireBank.setSections(null);
        questionnaireBankRepository.save(questionnaireBank);
        questionnaireBankRepository.delete(questionnaireBank);

        for (SectionBank sectionBank : sectionBankList) {
            deleteSection(sectionBank);
        }
    }

    private void deleteSection(SectionBank sectionBank) {
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

    private void deleteQuestion(QuestionBank questionBank) {
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

    private void deleteAnswer(AnswerBank answerBank) {
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
