package com.auth.service.app.services;

import com.auth.service.app.models.dto.UserDTO;
import com.auth.service.app.models.entity.User;
import com.auth.service.app.models.entity.UserRole;
import com.auth.service.app.models.repository.UserRepository;
import com.auth.service.app.models.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.exceptions.ErrorCode;
import nrt.common.microservice.exceptions.ResourceNotFoundException;
import nrt.common.microservice.models.repository.CommonEntityRepository;
import nrt.common.microservice.services.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl extends CommonServiceImpl<UserDTO, User> implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private PasswordService passwordService;
    @Override
    protected CommonEntityRepository<User> getCommonRepository() {
        return this.userRepository;
    }

    @Override
    protected Specification<User> getCustomSpecification(Object filter) {
        return null;
    }

    @Override
    protected User dtoToEntity(UserDTO dto) {
        return null;
    }

    @Override
    protected UserDTO entityToDto(User entity) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Enter to deleteById()");
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new ResourceNotFoundException(ErrorCode.NOT_FOUND_MESSAGE);

        // Delete first refresh_token with reference to user_id
        refreshTokenService.deleteByUserId(userOptional.get().getId());
        // Delete password_reset with references to user_id
        passwordService.deleteByUserId(userOptional.get().getId());
        // Delete roles of user
        List<UserRole> userRoleList = userRoleRepository.findByUser(userOptional.get());
        userRoleRepository.deleteAll(userRoleList);
        // Delete user
        userRepository.delete(userOptional.get());
        log.info("User successfully removed");
    }
}
