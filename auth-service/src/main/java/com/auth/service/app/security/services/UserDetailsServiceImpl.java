package com.auth.service.app.security.services;

import com.auth.service.app.models.entity.User;
import com.auth.service.app.models.entity.UserRole;
import com.auth.service.app.models.repository.UserRepository;
import com.auth.service.app.models.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Enter to loadUserByUsername()");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        user.setUserRoles(userRoles);
        return UserDetailsImpl.build(user);
    }

}
