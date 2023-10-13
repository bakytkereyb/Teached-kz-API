package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.CompetenceBank;
import diplomka.diplomkaapiapp.entities.competence.bank.ComponentBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionnaireBank;
import diplomka.diplomkaapiapp.repositories.competence.bank.CompetenceBankRepository;
import diplomka.diplomkaapiapp.repositories.competence.bank.ComponentBankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ComponentBankService {
    private final ComponentBankRepository componentBankRepository;
    private final CompetenceBankRepository competenceBankRepository;

    public ComponentBank saveComponentBank(ComponentBank componentBank) {
        return componentBankRepository.save(componentBank);
    }

    private CompetenceBank getCompetenceBank() {
        return competenceBankRepository.findByName("Competence Bank");
    }

    public void initComponents() {
        CompetenceBank competenceBank = getCompetenceBank();
//        ComponentBank com1 = new ComponentBank(
//                "Knowledgeable",
//                "Білімді",
//                "Знаниевый");
        ComponentBank com2 = new ComponentBank(
                "Didactic",
                "Дидактикалық",
                "Дидактический");
        ComponentBank com3 = new ComponentBank(
                "Design",
                "Дизайн",
                "Проектировочный");
//        ComponentBank com4 = new ComponentBank(
//                "Informational",
//                "Ақпараттық",
//                "Информационный");
        ComponentBank com5 = new ComponentBank(
                "Communicative",
                "Коммуникативті",
                "Коммуникативный");
        ComponentBank com6 = new ComponentBank(
                "Reflective",
                "Рефлексиялық",
                "Рефлексивный");
        ComponentBank com7 = new ComponentBank(
                "Monitoring",
                "Бақылау",
                "Мониторинговый");
        ComponentBank com8 = new ComponentBank(
                "Personal-motivational",
                "Жеке-мотивациялық",
                "Личностно-мотивационный");
//        if (componentBankRepository.findByName(com1.getName()) == null) {
//            componentBankRepository.save(com1);
//            competenceBank.addComponent(com1);
//            competenceBankRepository.save(competenceBank);
//        }
        if (componentBankRepository.findByName(com2.getName()) == null) {
            componentBankRepository.save(com2);
            competenceBank.addComponent(com2);
            competenceBankRepository.save(competenceBank);
        }
        if (componentBankRepository.findByName(com3.getName()) == null) {
            componentBankRepository.save(com3);
            competenceBank.addComponent(com3);
            competenceBankRepository.save(competenceBank);
        }
//        if (componentBankRepository.findByName(com4.getName()) == null) {
//            componentBankRepository.save(com4);
//            competenceBank.addComponent(com4);
//            competenceBankRepository.save(competenceBank);
//        }
        if (componentBankRepository.findByName(com5.getName()) == null) {
            componentBankRepository.save(com5);
            competenceBank.addComponent(com5);
            competenceBankRepository.save(competenceBank);
        }
        if (componentBankRepository.findByName(com6.getName()) == null) {
            componentBankRepository.save(com6);
            competenceBank.addComponent(com6);
            competenceBankRepository.save(competenceBank);
        }
        if (componentBankRepository.findByName(com7.getName()) == null) {
            componentBankRepository.save(com7);
            competenceBank.addComponent(com7);
            competenceBankRepository.save(competenceBank);
        }
        if (componentBankRepository.findByName(com8.getName()) == null) {
            componentBankRepository.save(com8);
            competenceBank.addComponent(com8);
            competenceBankRepository.save(competenceBank);
        }
    }

    public ComponentBank getComponentById(UUID id) {
        return componentBankRepository.findById(id).orElse(null);
    }

    public List<ComponentBank> getAllComponents() {
        return componentBankRepository.findAll();
    }

    public ComponentBank getComponentBankByQuestionnaire(QuestionnaireBank questionnaireBank) {
        return componentBankRepository.findByQuestionnaireBankListContaining(questionnaireBank);
    }
}
