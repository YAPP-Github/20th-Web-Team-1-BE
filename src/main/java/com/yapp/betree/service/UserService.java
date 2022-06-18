package com.yapp.betree.service;

import com.yapp.betree.domain.User;
import com.yapp.betree.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    Optional<User> findByOauthId(Long oauthId) {
        log.info("oauthId로 유저 조회 : oauthId = {}", oauthId);
        return userRepository.findByOauthId(oauthId);
    }

    User save(User user) {
        log.info("User 등록 : {}", user);
        return userRepository.save(user);
    }
}
