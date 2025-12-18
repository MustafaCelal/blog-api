package com.raptiye.blog.util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MarkdownService {

    private final Parser parser;
    private final HtmlRenderer renderer;
    private final PolicyFactory sanitizer;

    public MarkdownService() {
        List<Extension> extensions = Collections.singletonList(TablesExtension.create());

        this.parser = Parser.builder()
                .extensions(extensions)
                .build();

        this.renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();

        // OWASP Sanitizer Policy - Customized for Blog Content
        this.sanitizer = Sanitizers.FORMATTING
                .and(Sanitizers.BLOCKS)
                .and(Sanitizers.LINKS)
                .and(Sanitizers.TABLES)
                .and(Sanitizers.STYLES)
                .and(new org.owasp.html.HtmlPolicyBuilder()
                        .allowElements("pre", "code")
                        .allowAttributes("class").onElements("code") // for potential syntax highlighting
                        .toFactory());
    }

    public String translateToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }

        Node document = parser.parse(markdown);
        String unsafeHtml = renderer.render(document);

        return sanitizer.sanitize(unsafeHtml);
    }
}
