package com.raptiye.blog.repository;

import com.raptiye.blog.domain.Tag;
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
class TagRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    @Test
    void shouldFindByNameAndSlug() {
        Tag tag = Tag.builder().name("Spring Boot").slug("spring-boot").build();
        entityManager.persist(tag);
        entityManager.flush();

        Optional<Tag> byName = tagRepository.findByName("Spring Boot");
        Optional<Tag> bySlug = tagRepository.findBySlug("spring-boot");

        assertThat(byName).isPresent();
        assertThat(bySlug).isPresent();
        assertThat(byName.get().getSlug()).isEqualTo("spring-boot");
    }

    @Test
    void shouldCheckIfExistsByNameAndSlug() {
        Tag tag = Tag.builder().name("Java").slug("java").build();
        entityManager.persist(tag);
        entityManager.flush();

        assertThat(tagRepository.existsByName("Java")).isTrue();
        assertThat(tagRepository.existsBySlug("java")).isTrue();
        assertThat(tagRepository.existsByName("C#")).isFalse();
    }
}
