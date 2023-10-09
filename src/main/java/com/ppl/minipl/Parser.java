package com.ppl.minipl;

import java.util.ArrayList;
import java.util.List;

import static com.ppl.minipl.TokenType.*;

public class Parser {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int lineNumber = 1;

    Parser(String source) {
        this.source = source;
    }

    List<Token> parseTokens() {
        while (!isAtEnd()) {
            start = current;
            parseToken();
        }

        tokens.add(new Token(EOF, "", null, lineNumber));
        return tokens;
    }

    private void parseToken() {
        // parse individual token
        char c = advance();
        switch (c) {
            case '(': addToken(LPAREN   ); break;
            case ')': addToken(RPAREN   ); break;
            case '{': addToken(LBRACE   ); break;
            case '}': addToken(RBRACE   ); break;
            case '-': addToken(MINUS    ); break;
            case '+': addToken(PLUS     ); break;
            case '*': addToken(STAR     ); break;
            case ',': addToken(COMMA    ); break;
            case '.': addToken(DOT      ); break;
            case ';': addToken(SEMICOLON); break;

            case '!': addToken(matchNextChar('=') ? BEQ : BANG ); break;
            case '=': addToken(matchNextChar('=') ? EEQ : EQUAL); break;
            case '<': addToken(matchNextChar('=') ? LEQ : LTHAN); break;
            case '>': addToken(matchNextChar('=') ? GEQ : GTHAN); break;

            case '/':
                if (matchNextChar('/')) {
                    // check if next char is '/'; '//' indicates start of comment
                    // just continue advancing until end of line
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                lineNumber++;
                break;

            default:
                PL.error(lineNumber, "Unexpected character.");
                break;
        }
    }

    private boolean matchNextChar(char expected) {
        // this method peeks and advance at the same time
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        // this method peeks and advance at the same time
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        // this overload method is for tokens with literal values
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, lineNumber));
    }
}