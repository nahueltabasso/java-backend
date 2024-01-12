package com.auth.service.app.services;

import com.auth.service.app.error.ErrorCode;
import com.auth.service.app.models.dto.*;
import com.auth.service.app.models.entity.RefreshToken;
import com.auth.service.app.models.entity.Role;
import com.auth.service.app.models.entity.User;
import com.auth.service.app.models.entity.UserRole;
import com.auth.service.app.models.repository.RoleRepository;
import com.auth.service.app.models.repository.UserRepository;
import com.auth.service.app.models.repository.UserRoleRepository;
import com.auth.service.app.security.jwt.JwtProvider;
import com.auth.service.app.security.services.UserDetailsImpl;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import nrt.common.microservice.services.EmailService;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import nrt.common.microservice.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl extends CommonServiceImpl<UserDTO, User> implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Value("${app.security.failsAttemps}")
    private int failsAttemps;
    @Value("${google.clientId}")
    private String googleClientId;
    @Value("${google.commomPassword}")
    private String googleCommonPassword;

    @Override
    protected CommonEntityRepository<User> getCommonRepository() {
        return null;
    }

    @Override
    protected Specification<User> getCustomSpecification(Object filter) {
        return null;
    }

    @Override
    protected User dtoToEntity(UserDTO dto) {
        log.info("Enter to dtoToEntity()");
        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .googleUser(dto.getGoogleUser())
                .facebookUser(dto.getFacebookUser())
                .appleUser(dto.getAppleUser())
                .firstLogin(dto.getFirstLogin())
                .userLocked(dto.getUserLocked())
                .failsAttemps(dto.getFailsAttemps()).build();

        user.setCreationUser("admin");
        user.setCreationDateTime(LocalDateTime.now());
        user.setModificationUser("admin");
        user.setModificationDateTime(LocalDateTime.now());
        user.setVersion(1);
        user.setDeleted(false);
        return user;
    }

    @Override
    protected UserDTO entityToDto(User entity) {
        log.info("Enter to entityToDto()");

        List<UserRole> userRoles = userRoleRepository.findByUser(entity);
        List<RoleDTO> roleDTOList = new ArrayList<>();

        if (!ListUtils.isNullOrEmpty(userRoles)) {
            userRoles.stream().forEach(userRole -> {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(userRole.getRole().getId());
                roleDTO.setRoleName(userRole.getRole().getRoleName());
                roleDTOList.add(roleDTO);
            });
        }

        UserDTO userDTO = UserDTO.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .roles(roleDTOList)
                .googleUser(entity.getGoogleUser())
                .facebookUser(entity.getFacebookUser())
                .appleUser(entity.getAppleUser())
                .firstLogin(entity.getFirstLogin())
                .userLocked(entity.getUserLocked())
                .failsAttemps(entity.getFailsAttemps()).build();
        userDTO.setId(entity.getId());
        return userDTO;
    }

    @Override
    public MessageResponseDTO saveNewUser(UserDTO dto) {
        log.info("Enter to saveNewUser()");
        User userDb = null;
        try {
            Optional<User> user = userRepository.findByUsername(dto.getUsername());

            if (user.isPresent()) {
                throw new CommonBusinessException(ErrorCode.EXIST_USER_BY_USERNAME);
            }

            user = userRepository.findByEmail(dto.getEmail());
            if (user.isPresent()) {
                throw new CommonBusinessException(ErrorCode.EXIST_USER_BY_EMAIL);
            }

            if (!dto.getPassword().equalsIgnoreCase(dto.getConfirmPassword())) {
                throw new CommonBusinessException(ErrorCode.ERROR_PASSWORD_NOT_EQUALS);
            }

            userDb = dtoToEntity(dto);
            userDb.setPassword(passwordEncoder.encode(userDb.getPassword()));

            // Persist the User object in the database
            userDb = userRepository.save(userDb);
            log.info("Save new user with username -> {}",  userDb.getUsername());

            // After persisting the user we assign the roles to it
            List<RoleDTO> rolesDtoList = dto.getRoles();
            List<Role> roles = new ArrayList<>();

            if (ListUtils.isNullOrEmpty(rolesDtoList)) {
                // If the list of roles of the DTO is null, it implies that the user role
                // is assigned by default
                Role role = roleRepository.findByRoleName(Role.ROLE_USER);
                roles.add(role);
            }

            if (!ListUtils.isNullOrEmpty(rolesDtoList)) {
                rolesDtoList.stream().forEach(r -> {
                    if (isRoleValid(r)) {
                        Role role = roleRepository.findByRoleName(r.getRoleName());
                        roles.add(role);
                    }
                });
            }

            User finalUserDb = userDb;
            roles.stream().forEach(r -> {
                UserRole userRole = UserRole.builder()
                        .user(finalUserDb)
                        .role(r).build();
                userRoleRepository.save(userRole);
            });

            String template = "auth-template.html";
            String emailTo = userDb.getEmail();
            String username = userDb.getUsername();
            String subject = "Welcome!!!";

            AuthEmailDTO authEmailDTO = new AuthEmailDTO();
            authEmailDTO.setTemplate(template);
            authEmailDTO.setEmailTo(emailTo);
            authEmailDTO.setSubjetc(subject);
            authEmailDTO.setUsername(username);
            emailService.sendSimpleMail(authEmailDTO);

            return MessageResponseDTO.builder()
                    .httpStatus(HttpStatus.CREATED.value())
                    .message(messageSource.getMessage(ErrorCode.USER_CREATED, null, Locale.US))
                    .build();
        } catch (CommonBusinessException e) {
            log.error("");
            log.error(e.getMessage());
            if (e.getMessage().equals(ErrorCode.ROLE_NOT_VALID)) {
                Optional<User> user = userRepository.findByUsername(dto.getUsername());
                if (user.isPresent()) {
                    List<UserRole> userRoles = userRoleRepository.findByUser(user.get());
                    if (!ListUtils.isNullOrEmpty(userRoles)) userRoleRepository.deleteAll(userRoles);
                    userRepository.delete(user.get());
                }
            }
            e.printStackTrace();
            throw new CommonBusinessException(e.getMessage());
        }
    }

    @Override
    public LoginResponseDTO login(Authentication authentication) {
        log.info("Enter to login()");
        try {
            log.info("Generating token");
            String accessToken = jwtProvider.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<RoleDTO> roles = userDetails.getAuthorities().stream()
                    .map(role -> {
                        RoleDTO roleDTO = RoleDTO.builder().roleName(role.getAuthority()).build();
                        return roleDTO;
                    }).collect(Collectors.toList());

            // Generate a refresh token for response
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            User user = userRepository.findById(userDetails.getId()).get();

            LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                    .id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .email(userDetails.getEmail())
                    .roles(roles)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .type("Bearer")
                    .currentDateTime(LocalDateTime.now())
                    .firstLogin(user.getFirstLogin()).build();

            // Update firstLogin property
            if (user.getFirstLogin().equals(Boolean.TRUE)) {
                user.setFirstLogin(Boolean.FALSE);
                userRepository.save(user);
            }

            return loginResponseDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean checkUserLocked(String username) {
        log.info("Enter to checkUserLocked()");
        log.info("Check username = " + username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User userEntity = user.get();
            if (userEntity.getFailsAttemps() == failsAttemps) {
                userEntity.setUserLocked(Boolean.TRUE);
                userRepository.save(userEntity);
                return true;
            }
            if (!userEntity.getUserLocked()) return false;

            return true;
        }
        return false;
    }

    @Override
    public void updateFailsAttemps(String username) {
        log.info("Enter to updateFailsAttemps()");
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFailsAttemps(user.getFailsAttemps() + 1);
            userRepository.save(user);
        }
    }

    @Override
    public LoginResponseDTO validate(String token) {
        log.info("Enter to validate()");
        if (!jwtProvider.validateJwtToken(token))
            return null;
        String username = jwtProvider.getUsernameFromToken(token);
        if (!userRepository.findByUsername(username).isPresent())
            return null;
        return LoginResponseDTO.builder()
                .accessToken(token).build();
    }

    @Override
    public Map<String, Object> getCurrentUserDetails(String token) {
        log.info("Enter to getCurrentUserDetails()");
        String username = jwtProvider.getUsernameFromToken(token);
        log.info("Username = " + username);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        Map<String, Object> result = new HashMap<>();
        result.put("id", userDetails.getId());
        result.put("email", userDetails.getEmail());
        result.put("username", userDetails.getUsername());
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority()).collect(Collectors.toList());
        result.put("authorities", authorities);
        return result;
    }

    @Override
    public LoginResponseDTO googleLogin(String googleToken) {
        log.info("Enter to googleLogin()");
        try {
            final NetHttpTransport netHttpTransport = new NetHttpTransport();
            final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier.Builder verifier =
                    new GoogleIdTokenVerifier.Builder(netHttpTransport, jsonFactory)
                            .setAudience(Collections.singletonList(googleClientId));

            final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), googleToken);
            final GoogleIdToken.Payload payload = googleIdToken.getPayload();
            Optional<User> userOptional = userRepository.findByEmail(payload.getEmail());
            User user = saveGoogleUser(userOptional, payload);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), googleCommonPassword));

            return this.login(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private User saveGoogleUser(Optional<User> userOptional, GoogleIdToken.Payload payload) {
        User user = null;
        if (userOptional.isPresent()) {
            // User registered previous
            log.info("User founded - Username = " + userOptional.get().getUsername());
            user = userOptional.get();
            user.setPassword(passwordEncoder.encode(this.googleCommonPassword));
            user.setGoogleUser(Boolean.TRUE);
            userRepository.save(user);
        }

        if (!userOptional.isPresent()) {
            // New user
            log.info("User not founded with email = " + payload.getEmail());
            UserDTO userDTO = UserDTO.builder()
                    .username(payload.get("given_name").toString().concat(payload.get("family_name").toString()))
                    .email(payload.getEmail())
                    .password(googleCommonPassword)
                    .confirmPassword(googleCommonPassword)
                    .googleUser(Boolean.TRUE)
                    .facebookUser(Boolean.FALSE)
                    .appleUser(Boolean.FALSE)
                    .firstLogin(Boolean.TRUE)
                    .userLocked(Boolean.FALSE)
                    .roles(null)
                    .failsAttemps(0).build();

            user = this.dtoToEntity(userDTO);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user = userRepository.save(user);

            UserRole userRole = UserRole.builder()
                    .role(roleRepository.findByRoleName(Role.ROLE_USER))
                    .user(user).build();
            userRoleRepository.save(userRole);
        }
        return user;
    }

    private Boolean isRoleValid(RoleDTO roleDTO) {
        Boolean valid = false;
        switch (roleDTO.getRoleName()) {
            case Role.ROLE_USER -> valid = true;
            case Role.ROLE_ADMIN -> valid = true;
            case Role.ROLE_SUPERADMIN -> valid = true;
            default -> throw new CommonBusinessException(ErrorCode.ROLE_NOT_NULL);
        }
        return valid;
    }
}
