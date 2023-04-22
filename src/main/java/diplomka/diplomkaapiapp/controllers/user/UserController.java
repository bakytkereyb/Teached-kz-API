package diplomka.diplomkaapiapp.controllers.user;

import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.UserPut;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.user.RoleService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "User", description = "APIs for operation on User")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JwtService jwtService;

    @PostMapping("/save")
    public ResponseEntity saveUser(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   @RequestParam("firstName") String firstName,
                                   @RequestParam("secondName") String secondName,
                                   @RequestParam("email") String email) {
        try {
            User user = new User(
                    username,
                    password,
                    firstName,
                    secondName,
                    email);
            Role userRole = roleService.getRoleByName("user");
            user.addRole(userRole);
            return ResponseEntity.ok(userService.saveUser(user));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity updateUserById(@RequestBody UserPut userPut) {
        try {
            User user = userService.getUserByUsername(userPut.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            user.setUserPut(userPut);
            return ResponseEntity.ok(userService.updateUser(user));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/save/admin")
    public ResponseEntity saveUserAdmin(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   @RequestParam("firstName") String firstName,
                                   @RequestParam("secondName") String secondName,
                                   @RequestParam("email") String email) {
        try {
            User user = new User(
                    username,
                    password,
                    firstName,
                    secondName,
                    email);
            Role userRole = roleService.getRoleByName("admin");
            user.addRole(userRole);
            return ResponseEntity.ok(userService.saveUser(user));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{username}")
    @Operation(
            summary = "Get User by username",
            description = "Get User by username")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class))})
    })
    public ResponseEntity getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity getAllUsers(@RequestParam("skip") Integer skip,
                                      @RequestParam("limit") Integer limit) {
        try {
            List<User> users = userService.getAllUsers(skip, limit);

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/get/token")
    public ResponseEntity getUserByToken(@RequestParam("token") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
