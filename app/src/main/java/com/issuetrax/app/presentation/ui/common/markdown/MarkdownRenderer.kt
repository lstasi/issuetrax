package com.issuetrax.app.presentation.ui.common.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A simple markdown renderer for PR descriptions.
 * 
 * Supports:
 * - Headers (# H1, ## H2, ### H3)
 * - Bold (**text** or __text__)
 * - Italic (*text* or _text_)
 * - Code (`inline code`)
 * - Code blocks (```code```)
 * - Links ([text](url))
 * - Lists (- item or * item)
 * - Horizontal rules (---, ***)
 * 
 * This is a lightweight implementation optimized for mobile PR descriptions.
 * Does not support all markdown features (images, tables, nested lists, etc.)
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    SelectionContainer {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            parseMarkdown(markdown).forEach { block ->
                when (block) {
                    is MarkdownBlock.Header -> {
                        Text(
                            text = block.text,
                            style = when (block.level) {
                                1 -> MaterialTheme.typography.headlineMedium
                                2 -> MaterialTheme.typography.headlineSmall
                                else -> MaterialTheme.typography.titleLarge
                            },
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    is MarkdownBlock.Paragraph -> {
                        Text(
                            text = parseInlineMarkdown(block.text),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    is MarkdownBlock.CodeBlock -> {
                        Text(
                            text = block.code,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                                .horizontalScroll(rememberScrollState())
                        )
                    }
                    is MarkdownBlock.ListItem -> {
                        Text(
                            text = buildAnnotatedString {
                                append("• ")
                                append(parseInlineMarkdown(block.text))
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp)
                        )
                    }
                    is MarkdownBlock.HorizontalRule -> {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Parses markdown text into blocks.
 */
private fun parseMarkdown(markdown: String): List<MarkdownBlock> {
    val lines = markdown.lines()
    val blocks = mutableListOf<MarkdownBlock>()
    var i = 0
    
    while (i < lines.size) {
        val line = lines[i].trim()
        
        when {
            // Code block
            line.startsWith("```") -> {
                i++
                val codeLines = mutableListOf<String>()
                while (i < lines.size && !lines[i].trim().startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }
                blocks.add(MarkdownBlock.CodeBlock(codeLines.joinToString("\n")))
                i++ // Skip closing ```
            }
            // Header
            line.startsWith("#") -> {
                val level = line.takeWhile { it == '#' }.length
                val text = line.drop(level).trim()
                blocks.add(MarkdownBlock.Header(level, text))
                i++
            }
            // Horizontal rule
            line.matches(Regex("^-{3,}$")) || line.matches(Regex("^\\*{3,}$")) -> {
                blocks.add(MarkdownBlock.HorizontalRule)
                i++
            }
            // List item
            line.startsWith("- ") || line.startsWith("* ") -> {
                blocks.add(MarkdownBlock.ListItem(line.drop(2).trim()))
                i++
            }
            // Empty line
            line.isEmpty() -> {
                i++
            }
            // Paragraph
            else -> {
                blocks.add(MarkdownBlock.Paragraph(line))
                i++
            }
        }
    }
    
    return blocks
}

/**
 * Parses inline markdown formatting (bold, italic, code, links).
 */
@Composable
private fun parseInlineMarkdown(text: String) = buildAnnotatedString {
    var remaining = text
    
    while (remaining.isNotEmpty()) {
        // Try to find the next markdown pattern
        val boldMatch = Regex("\\*\\*(.+?)\\*\\*|__(.+?)__").find(remaining)
        val italicMatch = Regex("\\*(.+?)\\*|_(.+?)_").find(remaining)
        val codeMatch = Regex("`(.+?)`").find(remaining)
        val linkMatch = Regex("\\[(.+?)\\]\\((.+?)\\)").find(remaining)
        
        // Find the earliest match
        val matches = listOfNotNull(
            boldMatch?.let { it to "bold" },
            italicMatch?.let { it to "italic" },
            codeMatch?.let { it to "code" },
            linkMatch?.let { it to "link" }
        ).sortedBy { it.first.range.first }
        
        if (matches.isEmpty()) {
            // No more formatting, append the rest
            append(remaining)
            break
        }
        
        val (match, type) = matches.first()
        
        // Append text before the match
        append(remaining.substring(0, match.range.first))
        
        // Apply formatting
        when (type) {
            "bold" -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(match.groupValues[1].ifEmpty { match.groupValues[2] })
                }
            }
            "italic" -> {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(match.groupValues[1])
                }
            }
            "code" -> {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = MaterialTheme.colorScheme.surfaceVariant,
                        fontSize = 13.sp
                    )
                ) {
                    append(" ${match.groupValues[1]} ")
                }
            }
            "link" -> {
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(match.groupValues[1])
                }
            }
        }
        
        // Continue with the rest
        remaining = remaining.substring(match.range.last + 1)
    }
}

/**
 * Represents a block of markdown content.
 */
private sealed class MarkdownBlock {
    data class Header(val level: Int, val text: String) : MarkdownBlock()
    data class Paragraph(val text: String) : MarkdownBlock()
    data class CodeBlock(val code: String) : MarkdownBlock()
    data class ListItem(val text: String) : MarkdownBlock()
    object HorizontalRule : MarkdownBlock()
}
