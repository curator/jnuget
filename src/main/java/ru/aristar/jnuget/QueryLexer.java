package ru.aristar.jnuget;

import java.util.ArrayList;
import java.util.List;

/**
 * Лексический анализатор запросов
 *
 * @author sviridov
 */
public class QueryLexer {

    /**
     * @param string проверяемая строка
     * @return является ли строка границой группы
     */
    private boolean isBorderToken(String string) {
        return string.matches("[\\(\\)]");
    }

    /**
     * @param string проверяемая строка
     * @return является ли строка кавычками
     */
    private boolean isQuotesToken(String string) {
        return string.matches("['\"]");
    }

    /**
     * @param builder аккаомулятор символов
     * @param tokenString следующий символ
     * @return токен собран полностью
     */
    private boolean isTokenDone(StringBuilder builder, String tokenString) {
        if (builder.length() == 0) {
            return false;
        }
        String aggregateToken = builder.toString();
        if (isBorderToken(aggregateToken)) {
            return true;
        }
        if (isBorderToken(tokenString)) {
            return true;
        }
        if (isQuotesToken(aggregateToken)) {
            return true;
        }
        if (isQuotesToken(tokenString)) {
            return true;
        }
        if (isSkipToken(tokenString)) {
            return true;
        }
        return false;
    }

    /**
     * @param string проверяемая строка
     * @return является ли строка "пустым" токеном
     */
    private boolean isSkipToken(String string) {
        return string.matches("[\\s\\r\\n]+");
    }

    /**
     * Разделяет строку на лексемы
     *
     * @param value исходная строка
     * @return список лексем
     */
    protected List<String> split(String value) {
        char[] chars = value.toCharArray();
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String tokenString = new String(new char[]{c});
            if (isSkipToken(tokenString) && builder.length() == 0) {
                continue;
            }
            if (isTokenDone(builder, tokenString)) {
                tokens.add(builder.toString());
                builder = new StringBuilder();
            }
            if (!isSkipToken(tokenString)) {
                builder.append(tokenString);
                if (i == chars.length - 1) {
                    tokens.add(builder.toString());
                }
            }
        }
        return tokens;
    }
}
