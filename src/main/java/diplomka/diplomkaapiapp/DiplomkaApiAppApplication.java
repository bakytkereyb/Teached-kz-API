package diplomka.diplomkaapiapp;

import diplomka.diplomkaapiapp.constants.EnvironmentProperties;
import diplomka.diplomkaapiapp.entities.competence.bank.CompetenceBank;
import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.services.competence.bank.CompetenceBankService;
import diplomka.diplomkaapiapp.services.competence.bank.ComponentBankService;
import diplomka.diplomkaapiapp.services.storage.FilesStorageService;
import diplomka.diplomkaapiapp.services.user.RoleService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@SecurityScheme(
        type = SecuritySchemeType.APIKEY,
        name = "Authorization",
        in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(
        info = @Info(title = "TeachEd Documentation", version = "1.0.0"),
        security = { @SecurityRequirement(name = "Authorization") },
        servers = {
            @Server(url = EnvironmentProperties.SERVER_URL, description = "Default Server URL")
        }
)
public class DiplomkaApiAppApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(DiplomkaApiAppApplication.class, args);
        RoleService roleService = configurableApplicationContext.getBean(RoleService.class);
        UserService userService = configurableApplicationContext.getBean(UserService.class);
        CompetenceBankService competenceBankService = configurableApplicationContext.getBean(CompetenceBankService.class);
        ComponentBankService componentBankService = configurableApplicationContext.getBean(ComponentBankService.class);

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");
        Role trainerRole = new Role("trainer");
        userRole = roleService.saveRole(userRole);
        adminRole = roleService.saveRole(adminRole);
        trainerRole = roleService.saveRole(trainerRole);

        CompetenceBank competenceBank = new CompetenceBank("Competence Bank");

        competenceBankService.initCompetenceBank(competenceBank);

        FilesStorageService filesStorageService = configurableApplicationContext.getBean(FilesStorageService.class);
        filesStorageService.init();

        userService.initAdmin();
        userService.initDefaultTrainer();
        userService.initDefaultUser();

        componentBankService.initComponents();
    }
}
