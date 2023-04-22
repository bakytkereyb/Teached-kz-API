package diplomka.diplomkaapiapp;

import diplomka.diplomkaapiapp.entities.competence.bank.CompetenceBank;
import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.competence.bank.CompetenceBankService;
import diplomka.diplomkaapiapp.services.user.RoleService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@SecurityScheme(
        type = SecuritySchemeType.APIKEY,
        name = "Authorization",
        in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(
        info = @Info(title = "TeachEd Documentation", version = "1.0.0"),
        security = { @SecurityRequirement(name = "Authorization") })
public class DiplomkaApiAppApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(DiplomkaApiAppApplication.class, args);
        RoleService roleService = configurableApplicationContext.getBean(RoleService.class);
        UserService userService = configurableApplicationContext.getBean(UserService.class);
        CompetenceBankService competenceBankService = configurableApplicationContext.getBean(CompetenceBankService.class);

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");
        Role trainerRole = new Role("trainer");
        userRole = roleService.saveRole(userRole);
        adminRole = roleService.saveRole(adminRole);
        trainerRole = roleService.saveRole(trainerRole);

        CompetenceBank competenceBank = new CompetenceBank("Competence Bank");

        competenceBankService.initCompetenceBank(competenceBank);

//        Role createdAdminRole = roleService.getRoleByName("admin");
//        User user = new User(
//                "admin",
////                "$2a$10$Hq/pSglIWlsUSEyxDCxoNetcVwazyseFoTgAyq7RvLIi9bRjVkRlO",
//                "123",
//                "Admin",
//                "Adminovich",
//                "admin@admin.ru");
//        user.addRole(adminRole);
//        user = userService.saveUser(user);

    }
}
