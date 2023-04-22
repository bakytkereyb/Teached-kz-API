package diplomka.diplomkaapiapp.controllers.competence;

import diplomka.diplomkaapiapp.services.competence.bank.CompetenceBankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/competence-bank")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Competence Bank", description = "APIs for operation on Competence Bank")
public class CompetenceBankController {
    private final CompetenceBankService competenceBankService;

    @GetMapping("/get")
    public ResponseEntity getCompetenceBank() {
        try {
            return ResponseEntity.ok(competenceBankService.getCompetenceBank());
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
