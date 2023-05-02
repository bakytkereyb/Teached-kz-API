package diplomka.diplomkaapiapp.repositories.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.ComponentBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionnaireBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComponentBankRepository extends JpaRepository<ComponentBank, UUID>, PagingAndSortingRepository<ComponentBank, UUID> {
    ComponentBank findByQuestionnaireBankListContaining(QuestionnaireBank questionnaireBank);
}
