package com.raptiye.blog.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("cleanup")
@RequiredArgsConstructor
@Slf4j
public class DbCleaner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.warn("Cleaning up database tables...");

        jdbcTemplate.execute("DROP TABLE IF EXISTS post_tags CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS comments CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS posts CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS tags CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE");

        log.warn("Database cleanup completed. Please restart without 'cleanup' profile to run migrations.");
        System.exit(0);
    }
}
