package diplomka.diplomkaapiapp.controllers.competence;

import diplomka.diplomkaapiapp.services.competence.bank.ComponentBankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questionnaire-bank")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Questionnaire Bank", description = "APIs for operation on Questionnaire Bank")
public class QuestionnaireBankController {
    private final ComponentBankService componentBankService;
}
