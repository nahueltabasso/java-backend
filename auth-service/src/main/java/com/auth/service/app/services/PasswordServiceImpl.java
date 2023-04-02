package com.auth.service.app.services;

import com.auth.service.app.error.ErrorCode;
import com.auth.service.app.models.dto.AuthEmailDTO;
import com.auth.service.app.models.dto.PasswordRequestDTO;
import com.auth.service.app.models.entity.PasswordReset;
import com.auth.service.app.models.entity.User;
import com.auth.service.app.models.repository.PasswordResetRepository;
import com.auth.service.app.models.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.exceptions.CommonBusinessException;
import nrt.common.microservice.services.EmailService;
import nrt.common.microservice.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    private PasswordResetRepository passwordResetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public void requestPasswordChange(String email) {
        log.info("Enter to requestPasswordChange()");

        // First valid if the email has a correct format
        if (!StringUtils.validEmail(email)) {
            throw new CommonBusinessException(ErrorCode.EMAIL_INVALID);
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new CommonBusinessException(ErrorCode.USER_NOT_EXIST_BY_EMAIL);
        }

        String code = "";
        boolean flag = false;
        while (!flag) {
            code = getCode();
            PasswordReset pr = passwordResetRepository.findByCode(code);
            flag = pr == null ? true : false;
        }

        // Build entity and persist into database
        PasswordReset passwordReset = PasswordReset.builder()
                .user(userOptional.get())
                .code(code)
                .expiryDate(calculateExpiryDate(PasswordReset.EXPIRATION)).build();

        passwordReset = passwordResetRepository.save(passwordReset);

        // Send email to user with code
        String template = "password-reset-template.html";
        String emailTo = email;
        String username = userOptional.get().getUsername();
        String subject = "PASSWORD REQUEST CHANGE";
        String body = code;

        AuthEmailDTO authEmailDTO = new AuthEmailDTO();
        authEmailDTO.setTemplate(template);
        authEmailDTO.setEmailTo(emailTo);
        authEmailDTO.setSubjetc(subject);
        authEmailDTO.setUsername(username);
        authEmailDTO.setBody(body);
        emailService.sendSimpleMail(authEmailDTO);
    }

    @Override
    public void resetPassword(PasswordRequestDTO dto) {
        log.info("Enter to resetPassword()");

        // Retrieve the PasswordReset entity object by code
        PasswordReset passwordReset = passwordResetRepository.findByCode(dto.getCode());
        if (passwordReset == null || passwordReset.getCode().isEmpty()) {
            throw new CommonBusinessException(ErrorCode.CODE_RESET_PASSWORD_INVALID);
        }

        // TODO: Valid if the code not expired
        if (isCodeExpired(passwordReset)) {
            throw new CommonBusinessException(ErrorCode.CODE_EXPIRED);
        }

        // TODO: Valid if the both password are equals
        if (!confirmPassword(dto)) {
            throw new CommonBusinessException(ErrorCode.ERROR_PASSWORD_NOT_EQUALS);
        }

        User userDb = passwordReset.getUser();
        userDb.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        log.info("Change the user password to -> " + userDb.getUsername());
        userRepository.save(userDb);
    }

    @Override
    public void deleteByUserId(Long userId) {
        log.info("Enter to deleteByUserId()");
        passwordResetRepository.deleteByUserId(userId);
    }

    private String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            code = code + String.valueOf(random.nextInt(10));
        }
        return code;
    }

    private LocalDateTime calculateExpiryDate(int expiryTime) {
        Calendar calendar = Calendar.getInstance();
        Date date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MINUTE, expiryTime);
        date = calendar.getTime();
        LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDate;
    }

    private boolean isCodeExpired(PasswordReset passwordReset) {
        Calendar calendar = Calendar.getInstance();
        Date expiryDate = Date.from(passwordReset.getExpiryDate().atZone(ZoneId.systemDefault()).toInstant());
        return expiryDate.before(calendar.getTime());
    }

    private boolean confirmPassword(PasswordRequestDTO dto) {
        String newPass = dto.getNewPassword();
        String confirmPass = dto.getConfirmPassword();
        return newPass.equalsIgnoreCase(confirmPass);
    }
}
