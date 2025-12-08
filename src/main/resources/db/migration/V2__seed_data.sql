-- Seed Data Migration
-- This migration adds initial users, tags, posts, and comments for the blog

-- Insert default users
-- Users will be created by DataInitializer to ensure valid password hashing

-- Insert tags
INSERT INTO tags (name, slug, created_at) VALUES
    ('Java', 'java', CURRENT_TIMESTAMP),
    ('Spring Boot', 'spring-boot', CURRENT_TIMESTAMP),
    ('Web Development', 'web-development', CURRENT_TIMESTAMP),
    ('Database', 'database', CURRENT_TIMESTAMP),
    ('Tutorial', 'tutorial', CURRENT_TIMESTAMP)
ON CONFLICT (slug) DO NOTHING;

-- Insert sample posts
INSERT INTO posts (title, slug, summary, content, published, created_at, updated_at) VALUES
    (
        'Getting Started with Spring Boot',
        'getting-started-with-spring-boot',
        'A comprehensive guide to building your first Spring Boot application',
        'Spring Boot has revolutionized the way we build Java applications. In this tutorial, we''ll walk through creating a complete REST API from scratch.

## What is Spring Boot?

Spring Boot is an opinionated framework that simplifies the setup and configuration of Spring applications. It provides auto-configuration, embedded servers, and production-ready features out of the box.

## Key Features

1. **Auto-Configuration**: Automatically configures your application based on dependencies
2. **Embedded Servers**: No need to deploy WAR files
3. **Starter Dependencies**: Simplified dependency management
4. **Production-Ready**: Built-in health checks and metrics

Let''s dive in and build something amazing!',
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Understanding JPA Relationships',
        'understanding-jpa-relationships',
        'Master the art of entity relationships in JPA and Hibernate',
        'Entity relationships are fundamental to database design. In this post, we''ll explore the different types of relationships in JPA.

## Types of Relationships

### One-to-One
A one-to-one relationship means that each entity instance is related to a single instance of another entity.

### One-to-Many / Many-to-One
The most common relationship type. For example, a blog post has many comments.

### Many-to-Many
Multiple instances of one entity are related to multiple instances of another. Posts and tags are a perfect example.

## Best Practices

1. Always use `@JoinColumn` for better control
2. Consider fetch strategies carefully (LAZY vs EAGER)
3. Implement proper bidirectional helpers
4. Override equals() and hashCode() correctly',
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Building RESTful APIs with Spring',
        'building-restful-apis-with-spring',
        'Learn the best practices for creating robust REST APIs',
        'REST (Representational State Transfer) is an architectural style for distributed systems. Spring makes it incredibly easy to build RESTful APIs.

## REST Principles

1. **Stateless**: Each request contains all necessary information
2. **Client-Server**: Separation of concerns
3. **Cacheable**: Responses must define themselves as cacheable or not
4. **Uniform Interface**: Consistent API design

## HTTP Methods

- **GET**: Retrieve resources
- **POST**: Create new resources
- **PUT**: Update entire resources
- **PATCH**: Partial updates
- **DELETE**: Remove resources

## Response Codes

Using the right HTTP status codes is crucial:
- 200 OK: Success
- 201 Created: Resource created
- 400 Bad Request: Invalid input
- 404 Not Found: Resource doesn''t exist
- 500 Internal Server Error: Server-side error',
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (slug) DO NOTHING;

-- Link posts with tags
INSERT INTO post_tags (post_id, tag_id)
SELECT p.id, t.id FROM posts p, tags t
WHERE (p.slug = 'getting-started-with-spring-boot' AND t.slug IN ('java', 'spring-boot', 'tutorial'))
   OR (p.slug = 'understanding-jpa-relationships' AND t.slug IN ('java', 'database', 'tutorial'))
   OR (p.slug = 'building-restful-apis-with-spring' AND t.slug IN ('spring-boot', 'web-development', 'tutorial'))
ON CONFLICT DO NOTHING;

-- Insert sample comments
INSERT INTO comments (post_id, author_name, author_email, content, approved, created_at)
SELECT p.id, 'John Doe', 'john@example.com', 'Great tutorial! Very clear and easy to follow.', true, CURRENT_TIMESTAMP
FROM posts p WHERE p.slug = 'getting-started-with-spring-boot'
ON CONFLICT DO NOTHING;

INSERT INTO comments (post_id, author_name, author_email, content, approved, created_at)
SELECT p.id, 'Jane Smith', 'jane@example.com', 'This helped me get started with my first Spring Boot project. Thanks!', true, CURRENT_TIMESTAMP
FROM posts p WHERE p.slug = 'getting-started-with-spring-boot'
ON CONFLICT DO NOTHING;

INSERT INTO comments (post_id, author_name, author_email, content, approved, created_at)
SELECT p.id, 'Alice Johnson', 'alice@example.com', 'The explanation of bidirectional relationships was particularly helpful!', true, CURRENT_TIMESTAMP
FROM posts p WHERE p.slug = 'understanding-jpa-relationships'
ON CONFLICT DO NOTHING;
