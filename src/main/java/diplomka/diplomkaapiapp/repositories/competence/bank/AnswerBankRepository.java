package diplomka.diplomkaapiapp.repositories.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.AnswerBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnswerBankRepository extends JpaRepository<AnswerBank, UUID>, PagingAndSortingRepository<AnswerBank, UUID> {

}
