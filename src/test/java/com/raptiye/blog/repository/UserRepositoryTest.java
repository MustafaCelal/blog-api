package com.raptiye.blog.repository;

import com.raptiye.blog.domain.User;
import com.raptiye.blog.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.raptiye.blog.config.JpaConfig.class)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByUsernameAndEmail() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedpass")
                .role(Role.USER)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> byUser = userRepository.findByUsername("testuser");
        Optional<User> byEmail = userRepository.findByEmail("test@example.com");

        assertThat(byUser).isPresent();
        assertThat(byEmail).isPresent();
        assertThat(byUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldCheckIfExistsByUsernameAndEmail() {
        User user = User.builder()
                .username("admin")
                .email("admin@blog.com")
                .password("pass")
                .role(Role.ADMIN)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        assertThat(userRepository.existsByUsername("admin")).isTrue();
        assertThat(userRepository.existsByEmail("admin@blog.com")).isTrue();
        assertThat(userRepository.existsByUsername("unknown")).isFalse();
    }
}
