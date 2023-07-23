package com.example.naejango.domain.user.repository;

import com.example.naejango.domain.user.domain.Gender;
import com.example.naejango.domain.user.domain.Role;
import com.example.naejango.domain.user.domain.User;
import com.example.naejango.domain.user.domain.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserProfileRepository userProfileRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    @DisplayName("saveUser : user 저장")
    void saveTest() {
        // given
        User testUser = User.builder()
                .userKey("TEST_1234")
                .role(Role.USER)
                .password("NULL")
                .signature("REFRESH_TOKEN")
                .userProfile(null)
                .build();

        // when
        userRepository.save(testUser);

        // then
        User findUser = userRepository.findById(testUser.getId()).orElseGet(() -> {
            return User.builder().userKey("FAIL").build();
        });
        assertEquals(findUser.getUserKey(), testUser.getUserKey());
    }

    @Test
    @Transactional
    @DisplayName("findUserWithProfile : User, UserProfile 조회")
    public void findUserWithProfileTest() {
        // given
        User testUser = User.builder()
                .userKey("TEST_1234")
                .role(Role.USER)
                .password("NULL")
                .signature("REFRESH_TOKEN")
                .userProfile(null)
                .build();

        UserProfile testUserProfile = UserProfile.builder()
                .birth("")
                .gender(Gender.Male)
                .phoneNumber("010-0000-0000")
                .nickname("Nick")
                .build();

        userRepository.save(testUser);
        userProfileRepository.save(testUserProfile);
        testUser.createUserProfile(testUserProfile);

        em.flush();
        em.clear();

        // when
        User findUser = userRepository.findUserWithProfileById(testUser.getId()).orElseGet(() -> {
            return User.builder().userKey("FAIL").build();
        });

        // then
        assertNotEquals("FAIL", findUser.getUserKey());
        assertEquals(findUser.getUserProfile().getNickname(), testUserProfile.getNickname());
    }

    @Test
    @Transactional
    @DisplayName("deleteUser : User 삭제")
    public void deleteUserTest() {
        // given
        User testUser = User.builder()
                .userKey("TEST_1234")
                .role(Role.USER)
                .password("NULL")
                .signature("REFRESH_TOKEN")
                .userProfile(null)
                .build();

        userRepository.save(testUser);

        em.flush();
        em.clear();

        // when
        User savedUser = userRepository.findByUserKey("TEST_1234").orElseGet(() -> {
            return User.builder().userKey("FAIL").build();
        });

        userRepository.deleteUserById(savedUser.getId());

        em.flush();
        em.clear();

        // then
        User findUser = userRepository.findByUserKey("TEST_1234").orElseGet(() -> {
            return User.builder().userKey("SUCCESS").build();
        });

        assertEquals("SUCCESS", findUser.getUserKey());
    }

    @Test
    @Transactional
    @DisplayName("delete : UserProfile 삭제")
    public void deleteUserProfileTest() {
        // given
        UserProfile testUserProfile = UserProfile.builder()
                .birth("")
                .gender(Gender.Male)
                .phoneNumber("010-0000-0000")
                .nickname("Nick")
                .build();

        userProfileRepository.save(testUserProfile);
        Long saveduUserProfileId = testUserProfile.getId();

        // when
        userProfileRepository.deleteById(saveduUserProfileId);
        em.flush();
        em.clear();

        // then
        UserProfile findUserProfile = userProfileRepository.findById(saveduUserProfileId).orElseGet(() -> {
            return UserProfile.builder().nickname("SUCCESS").build();
        });

        assertEquals("SUCCESS", findUserProfile.getNickname());
    }


}