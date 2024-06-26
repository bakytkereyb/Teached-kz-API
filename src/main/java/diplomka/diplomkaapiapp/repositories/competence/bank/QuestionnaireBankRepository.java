package diplomka.diplomkaapiapp.repositories.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.QuestionnaireBank;
import diplomka.diplomkaapiapp.entities.competence.bank.SectionBank;
import diplomka.diplomkaapiapp.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionnaireBankRepository extends JpaRepository<QuestionnaireBank, UUID>, PagingAndSortingRepository<QuestionnaireBank, UUID> {
    QuestionnaireBank findBySectionBankListContaining(SectionBank sectionBank);
    QuestionnaireBank findByIdAndPassedUsersContaining(UUID id, User user);
}
