package diplomka.diplomkaapiapp.controllers.competence;

import diplomka.diplomkaapiapp.entities.competence.bank.CompetenceBank;
import diplomka.diplomkaapiapp.entities.competence.bank.ComponentBank;
import diplomka.diplomkaapiapp.services.competence.bank.CompetenceBankService;
import diplomka.diplomkaapiapp.services.competence.bank.ComponentBankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/component-bank")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Component Bank", description = "APIs for operation on Component Bank")
public class ComponentBankController {
    private final CompetenceBankService competenceBankService;
    private final ComponentBankService componentBankService;

    @PostMapping("/save")
    public ResponseEntity saveComponent(@RequestParam("name") String name,
                                        @RequestParam("nameKz") String nameKz,
                                        @RequestParam("nameRu") String nameRu) {
        try {
            CompetenceBank competenceBank = competenceBankService.getCompetenceBank();
            if (competenceBank == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("competence bank not found");
            }
            ComponentBank componentBank = new ComponentBank(name, nameKz, nameRu);
            componentBank = componentBankService.saveComponentBank(componentBank);

            competenceBank.addComponent(componentBank);
            competenceBankService.saveCompetenceBank(competenceBank);
            return ResponseEntity.ok(componentBank);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getComponentById(@PathVariable UUID id) {
        try {
            ComponentBank componentBank = componentBankService.getComponentById(id);
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
