package com.auth.service.app.controllers;

import com.auth.service.app.error.ErrorCode;
import com.auth.service.app.models.dto.*;
import com.auth.service.app.models.entity.RefreshToken;
import com.auth.service.app.security.jwt.JwtProvider;
import com.auth.service.app.services.AuthService;
import com.auth.service.app.services.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.controllers.CommonController;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/security/auth")
@Slf4j
public class AuthController extends CommonController<UserDTO, UserDTO> {

    @Autowired
    private AuthService authService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private MessageSource messageSource;
    @Value("${google.clientId}")
    private String googleClientId;

    @Override
    protected CommonService getCommonService() {
        return this.authService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerNewUser(@RequestBody @Validated UserDTO userDTO, BindingResult result) {
        log.info("Enter to registerNewUser()");
        MessageResponseDTO dto = null;
        try {
            if (result.hasErrors()) {
                return validateBody(result);
            }
            dto = authService.saveNewUser(userDTO);
        } catch (CommonBusinessException e) {
            e.printStackTrace();
            return responseApiError(e.getMessage());
        }
        return ResponseEntity.status(dto.getHttpStatus()).body(dto);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        log.info("Enter to login()");
        log.info("Login -> remoteAddress = " + request.getRemoteAddr());
        log.info("Login -> username = " + loginRequestDTO.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()));

            // Check if the user is locked or not
            boolean userLocked = authService.checkUserLocked(loginRequestDTO.getUsername());
            log.info("Validated if the " + loginRequestDTO.getUsername() + " is locked");
            if (userLocked) {
                log.warn(loginRequestDTO.getUsername() + " locked!");
                throw new CommonBusinessException(ErrorCode.USER_LOCKED);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate the response of the login request
            LoginResponseDTO loginResponseDTO = authService.login(authentication);
            log.info("Login -> Successful login for user = " + loginRequestDTO.getUsername());

            return ResponseEntity.ok(loginResponseDTO);
        } catch (CommonBusinessException e) {
            e.printStackTrace();
            log.warn("User " + loginRequestDTO.getUsername() + " is locked");
            return responseApiError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Login incorrect: Bad credentials!");
            // Update the fails attemps if the username corresponds to one in the database
            // This property only is updating if the user enter a wrong password
            authService.updateFailsAttemps(loginRequestDTO.getUsername());
            return responseApiError("BAD_CREDENTIALS");
        }
    }

    @PostMapping("/google-sign-in")
    public ResponseEntity<?> googleSignIn(@RequestParam String googleToken) {
        log.info("Enter to googleSignIn()");
        log.info("Google TokenId = " + googleToken);
        return ResponseEntity.ok().body(authService.googleLogin(googleToken));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        log.info("Enter validateToken()");
        LoginResponseDTO dto = authService.validate(token);
        if (dto == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        log.info("Enter to refreshToken()");

        String token = refreshTokenRequestDTO.getRefreshToken();
        return refreshTokenService.getByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtProvider.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new RefreshTokenResponseDTO(accessToken, token, "Bearer"));
                }).orElseThrow(() -> new CommonBusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    @GetMapping("/get-user-details")
    public ResponseEntity<?> getUserDetails(@RequestParam("token") String token) {
        log.info("Enter to getUserDetails()");
        return ResponseEntity.ok(authService.getCurrentUserDetails(token));
    }

}
