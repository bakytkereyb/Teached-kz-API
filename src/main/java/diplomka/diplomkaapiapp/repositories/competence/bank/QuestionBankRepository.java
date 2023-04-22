package diplomka.diplomkaapiapp.repositories.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.AnswerBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, UUID>, PagingAndSortingRepository<QuestionBank, UUID> {
}
