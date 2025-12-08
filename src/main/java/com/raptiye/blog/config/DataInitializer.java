package com.raptiye.blog.config;

import com.raptiye.blog.domain.Comment;
import com.raptiye.blog.domain.Post;
import com.raptiye.blog.domain.Role;
import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.domain.User;
import com.raptiye.blog.repository.CommentRepository;
import com.raptiye.blog.repository.PostRepository;
import com.raptiye.blog.repository.TagRepository;
import com.raptiye.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final PostRepository postRepository;
        private final TagRepository tagRepository;
        private final CommentRepository commentRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) {
                log.info("Checking database initialization status...");

                // Always create default users if they don't exist
                if (userRepository.count() == 0) {
                        log.info("Creating default users...");

                        // Create admin user
                        User admin = User.builder()
                                        .username("admin")
                                        .email("admin@blog.com")
                                        .password(passwordEncoder.encode("admin123"))
                                        .role(Role.ADMIN)
                                        .build();
                        userRepository.save(admin);

                        // Create a regular user
                        User user = User.builder()
                                        .username("user")
                                        .email("user@blog.com")
                                        .password(passwordEncoder.encode("user123"))
                                        .role(Role.USER)
                                        .build();
                        userRepository.save(user);

                        log.info("Created {} users", userRepository.count());
                } else {
                        log.info("Users already exist. Skipping user creation.");
                }

                // Check if blog data already exists to prevent duplicate initialization
                if (postRepository.count() > 0) {
                        log.info("Blog data already exists. Skipping blog data initialization.");
                        return;
                }

                log.info("Initializing database with seed blog data...");

                // Create tags
                Tag javaTag = createTag("Java", "java");
                Tag springTag = createTag("Spring Boot", "spring-boot");
                Tag webDevTag = createTag("Web Development", "web-development");
                Tag databaseTag = createTag("Database", "database");
                Tag tutorialTag = createTag("Tutorial", "tutorial");

                log.info("Created {} tags", tagRepository.count());

                // Create posts
                Post post1 = createPost(
                                "Getting Started with Spring Boot",
                                "A comprehensive guide to building your first Spring Boot application",
                                "Spring Boot has revolutionized the way we build Java applications. In this tutorial, we'll walk through creating a complete REST API from scratch.\n\n"
                                                +
                                                "## What is Spring Boot?\n\n" +
                                                "Spring Boot is an opinionated framework that simplifies the setup and configuration of Spring applications. It provides auto-configuration, embedded servers, and production-ready features out of the box.\n\n"
                                                +
                                                "## Key Features\n\n" +
                                                "1. **Auto-Configuration**: Automatically configures your application based on dependencies\n"
                                                +
                                                "2. **Embedded Servers**: No need to deploy WAR files\n" +
                                                "3. **Starter Dependencies**: Simplified dependency management\n" +
                                                "4. **Production-Ready**: Built-in health checks and metrics\n\n" +
                                                "Let's dive in and build something amazing!",
                                true,
                                Arrays.asList(javaTag, springTag, tutorialTag));

                Post post2 = createPost(
                                "Understanding JPA Relationships",
                                "Master the art of entity relationships in JPA and Hibernate",
                                "Entity relationships are fundamental to database design. In this post, we'll explore the different types of relationships in JPA.\n\n"
                                                +
                                                "## Types of Relationships\n\n" +
                                                "### One-to-One\n" +
                                                "A one-to-one relationship means that each entity instance is related to a single instance of another entity.\n\n"
                                                +
                                                "### One-to-Many / Many-to-One\n" +
                                                "The most common relationship type. For example, a blog post has many comments.\n\n"
                                                +
                                                "### Many-to-Many\n" +
                                                "Multiple instances of one entity are related to multiple instances of another. Posts and tags are a perfect example.\n\n"
                                                +
                                                "## Best Practices\n\n" +
                                                "1. Always use `@JoinColumn` for better control\n" +
                                                "2. Consider fetch strategies carefully (LAZY vs EAGER)\n" +
                                                "3. Implement proper bidirectional helpers\n" +
                                                "4. Override equals() and hashCode() correctly",
                                true,
                                Arrays.asList(javaTag, databaseTag, tutorialTag));

                Post post3 = createPost(
                                "Building RESTful APIs with Spring",
                                "Learn the best practices for creating robust REST APIs",
                                "REST (Representational State Transfer) is an architectural style for distributed systems. Spring makes it incredibly easy to build RESTful APIs.\n\n"
                                                +
                                                "## REST Principles\n\n" +
                                                "1. **Stateless**: Each request contains all necessary information\n" +
                                                "2. **Client-Server**: Separation of concerns\n" +
                                                "3. **Cacheable**: Responses must define themselves as cacheable or not\n"
                                                +
                                                "4. **Uniform Interface**: Consistent API design\n\n" +
                                                "## HTTP Methods\n\n" +
                                                "- **GET**: Retrieve resources\n" +
                                                "- **POST**: Create new resources\n" +
                                                "- **PUT**: Update entire resources\n" +
                                                "- **PATCH**: Partial updates\n" +
                                                "- **DELETE**: Remove resources\n\n" +
                                                "## Response Codes\n\n" +
                                                "Using the right HTTP status codes is crucial:\n" +
                                                "- 200 OK: Success\n" +
                                                "- 201 Created: Resource created\n" +
                                                "- 400 Bad Request: Invalid input\n" +
                                                "- 404 Not Found: Resource doesn't exist\n" +
                                                "- 500 Internal Server Error: Server-side error",
                                true,
                                Arrays.asList(springTag, webDevTag, tutorialTag));

                Post post4 = createPost(
                                "Database Migration Strategies",
                                "How to handle database changes in production",
                                "Managing database changes in production environments is challenging. Let's explore different migration strategies.\n\n"
                                                +
                                                "## Migration Tools\n\n" +
                                                "### Flyway\n" +
                                                "Flyway is a version control system for databases. It uses SQL scripts or Java migrations.\n\n"
                                                +
                                                "### Liquibase\n" +
                                                "Liquibase provides database-independent migrations using XML, YAML, JSON, or SQL.\n\n"
                                                +
                                                "## Best Practices\n\n" +
                                                "1. **Never modify existing migrations**: Create new ones instead\n" +
                                                "2. **Test migrations**: Always test in a staging environment\n" +
                                                "3. **Backup before migration**: Safety first!\n" +
                                                "4. **Version control**: Keep migrations in Git\n" +
                                                "5. **Backward compatibility**: Ensure old code works with new schema\n\n"
                                                +
                                                "Remember: Database migrations are irreversible operations that require careful planning!",
                                true,
                                Arrays.asList(databaseTag, tutorialTag));

                createPost(
                                "Spring Boot Configuration Deep Dive",
                                "Mastering application configuration in Spring Boot",
                                "Configuration management is crucial for modern applications. Spring Boot provides several powerful ways to configure your application.\n\n"
                                                +
                                                "## Configuration Sources\n\n" +
                                                "Spring Boot loads configuration from multiple sources in this order:\n"
                                                +
                                                "1. Command line arguments\n" +
                                                "2. Environment variables\n" +
                                                "3. application.properties or application.yml\n" +
                                                "4. Default values\n\n" +
                                                "## Profiles\n\n" +
                                                "Profiles allow you to segregate configuration for different environments:\n"
                                                +
                                                "- development\n" +
                                                "- testing\n" +
                                                "- production\n\n" +
                                                "## Externalized Configuration\n\n" +
                                                "Keep sensitive data out of your codebase:\n" +
                                                "- Use environment variables for secrets\n" +
                                                "- Consider Spring Cloud Config for distributed systems\n" +
                                                "- Use @ConfigurationProperties for type-safe configuration\n\n" +
                                                "Configuration is the foundation of flexible, maintainable applications!",
                                false,
                                Arrays.asList(javaTag, springTag));

                log.info("Created {} posts", postRepository.count());

                // Create comments for posts
                createComment(post1, "John Doe", "john@example.com",
                                "Great tutorial! Very clear and easy to follow. Looking forward to more.", true);
                createComment(post1, "Jane Smith", "jane@example.com",
                                "This helped me get started with my first Spring Boot project. Thanks!", true);
                createComment(post1, "Bob Wilson", "bob@example.com",
                                "Could you cover Spring Security in a future post?", true);

                createComment(post2, "Alice Johnson", "alice@example.com",
                                "The explanation of bidirectional relationships was particularly helpful!", true);
                createComment(post2, "Charlie Brown", "charlie@example.com",
                                "I was struggling with fetch types. This cleared it up for me.", true);

                createComment(post3, "David Lee", "david@example.com",
                                "Excellent coverage of REST principles. The HTTP status codes section is a great reference.",
                                true);
                createComment(post3, "Emma White", "emma@example.com",
                                "Would love to see examples of exception handling in REST APIs.", true);

                createComment(post4, "Frank Miller", "frank@example.com",
                                "We use Flyway at our company. This guide is spot on!", true);

                log.info("Created {} comments", commentRepository.count());

                log.info("Database initialization completed successfully!");
                log.info("Summary: {} posts, {} tags, {} comments",
                                postRepository.count(), tagRepository.count(), commentRepository.count());
        }

        private Tag createTag(String name, String slug) {
                Tag tag = Tag.builder()
                                .name(name)
                                .slug(slug)
                                .build();
                return tagRepository.save(tag);
        }

        private Post createPost(String title, String summary, String content,
                        boolean published, List<Tag> tags) {
                Post post = Post.builder()
                                .title(title)
                                .summary(summary)
                                .content(content)
                                .published(published)
                                .build();

                // Add tags
                tags.forEach(post::addTag);

                return postRepository.save(post);
        }

        private Comment createComment(Post post, String authorName, String authorEmail,
                        String content, boolean approved) {
                Comment comment = Comment.builder()
                                .authorName(authorName)
                                .authorEmail(authorEmail)
                                .content(content)
                                .approved(approved)
                                .build();

                post.addComment(comment);
                return commentRepository.save(comment);
        }
}
