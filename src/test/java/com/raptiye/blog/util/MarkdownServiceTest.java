package com.raptiye.blog.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MarkdownServiceTest {

    private final MarkdownService markdownService = new MarkdownService();

    @Test
    void shouldTranslateMarkdownToHtml() {
        String markdown = "# Hello\nThis is **bold**";
        String html = markdownService.translateToHtml(markdown);

        assertThat(html).contains("<h1>Hello</h1>");
        assertThat(html).contains("<strong>bold</strong>");
    }

    @Test
    void shouldReturnEmptyStringForNullOrEmpty() {
        assertThat(markdownService.translateToHtml(null)).isEmpty();
        assertThat(markdownService.translateToHtml("")).isEmpty();
        assertThat(markdownService.translateToHtml("   ")).isEmpty();
    }

    @Test
    void shouldSanitizeDangerousHtml() {
        String markdown = "[click me](javascript:alert('xss'))";
        String html = markdownService.translateToHtml(markdown);

        assertThat(html).doesNotContain("javascript:");
    }

    @Test
    void shouldAllowTables() {
        String markdown = "| col 1 | col 2 |\n|---|---|\n| val 1 | val 2 |";
        String html = markdownService.translateToHtml(markdown);

        assertThat(html).contains("<table>");
        assertThat(html).contains("<td>val 1</td>");
    }
}
