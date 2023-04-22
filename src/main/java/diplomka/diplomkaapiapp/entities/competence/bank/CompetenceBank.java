package diplomka.diplomkaapiapp.entities.competence.bank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "competence_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceBank {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ComponentBank> componentBankList;

    public void addComponent(ComponentBank componentBank) {
        componentBankList.add(componentBank);
    }
}
