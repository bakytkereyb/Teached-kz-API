package diplomka.diplomkaapiapp.controllers.user;

import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.entities.user.University;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.ListPagination.ListPagination;
import diplomka.diplomkaapiapp.request.UserPut;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.user.RoleService;
import diplomka.diplomkaapiapp.services.user.UniversityService;
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
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private final UniversityService universityService;

    @PostMapping("/save")
    public ResponseEntity saveUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("firstName") String firstName,
            @RequestParam("secondName") String secondName,
            @RequestParam("email") String email,
            @RequestParam("universityId") UUID universityId
        ) {
        try {
            University university = universityService.getUniversityById(universityId);
            if (university == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("university not found");
            }

            User user = new User(
                    username,
                    password,
                    firstName,
                    secondName,
                    email,
                    university);
            Role userRole = roleService.getRoleByName("user");
            user.addRole(userRole);
            return ResponseEntity.ok(userService.saveUser(user));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/send/confirmation")
    public ResponseEntity sendConfirmation(@PathVariable UUID id,
                                           @RequestParam("email") String email) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            user.setEmail(email);
            user = userService.updateUser(user);
            userService.sendEmailConfirmationMessage(user);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/confirm/{token}")
    public ResponseEntity confirmUserEmail(@PathVariable String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            user.setIsConfirmed(true);
            user = userService.updateUser(user);
            return ResponseEntity.ok(new String("confirmed"));
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
                    email,
                    null);
            Role userRole = roleService.getRoleByName("admin");
            user.addRole(userRole);
            return ResponseEntity.ok(userService.saveUser(user));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/save/trainer")
    public ResponseEntity saveUserTrainer(@RequestParam("username") String username,
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
                    email,
                    null);
            Role userRole = roleService.getRoleByName("trainer");
            user.addRole(userRole);
            return ResponseEntity.ok(userService.saveUser(user));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/change/image")
    public ResponseEntity changeImageOfUser(@PathVariable UUID id,
                                            @RequestParam("imageFileName") String imageFileName) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            user.setImageFileName(imageFileName);
            user = userService.updateUser(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/role/add")
    public ResponseEntity addRoleToUser(@PathVariable UUID id,
                                        @RequestParam("roleName") String roleName) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            Role userRole = roleService.getRoleByName(roleName);
            if (userRole == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("role not found");
            }
            user.addRole(userRole);
            user = userService.updateUser(user);
            return ResponseEntity.ok(user);
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

    @GetMapping("/search")
    public ResponseEntity getUsersByFullName(@RequestParam("page") Integer page,
                                             @RequestParam("limit") Integer limit,
                                             @RequestParam("name") String name) {
        try {
            List<User> users = userService.getAllUsersByName(name, page, limit);
            List<User> nextUsers = userService.getAllUsersByName(name, page + 1, limit);
            ListPagination<User> listPagination = new ListPagination<>(users, !nextUsers.isEmpty());

            return ResponseEntity.ok(listPagination);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity getAllUsers(@RequestParam("page") Integer page,
                                      @RequestParam("limit") Integer limit) {
        try {
            List<User> users = userService.getAllUsers(page, limit);
            List<User> nextUsers = userService.getAllUsers(page + 1, limit);

            ListPagination<User> listPagination = new ListPagination<>(users, !nextUsers.isEmpty());

            return ResponseEntity.ok(listPagination);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

//    @GetMapping("/get/trainer")
//    public ResponseEntity getAllUsersTrainer(@RequestParam("skip") Integer skip,
//                                             @RequestParam("limit") Integer limit) {
//        try {
//            Role userRole = roleService.getRoleByName("trainer");
//            List<User> users = userService.getAllUsersByRole(skip, limit, userRole);
//
//            return ResponseEntity.ok(users);
//        } catch (Exception e) {
//            log.error(e.toString());
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }

    @GetMapping("/get/trainer/all")
    public ResponseEntity getAllUsersTrainerAll() {
        try {
            Role userRole = roleService.getRoleByName("trainer");
            List<User> users = userService.getAllUsersByRoleWithoutPage(userRole);

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
