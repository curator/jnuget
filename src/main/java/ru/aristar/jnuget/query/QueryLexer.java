package ru.aristar.jnuget.query;

import java.util.*;
import ru.aristar.jnuget.files.NugetFormatException;

/**
 * Лексический анализатор запросов
 *
 * @author sviridov
 */
public class QueryLexer {

    public static void assertToken(String actual, String expected) throws NugetFormatException {
        if (!Objects.equals(actual, expected)) {
            throw new NugetFormatException("Встретился токен '" + actual
                    + "', когда ожидался '" + expected + "'");
        }
    }

    private void checkForAndExpression(Stack<Expression> stack, Expression expression) {
        if (!stack.isEmpty() && stack.peek() instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) stack.pop();
            andExpression.secondExpression = expression;
            stack.push(andExpression);
        } else {
            stack.push(expression);
        }
    }

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

    protected Expression parse(Queue<String> tokens, Stack<Expression> stack) throws NugetFormatException {
        if (tokens.isEmpty()) {
            return stack.pop();
        }
        String token = tokens.poll();

        if (stack == null) {
            stack = new Stack<>();
        }
        switch (token) {
            case "(": {
                GroupExpression expression = new GroupExpression();
                expression.innerExpression = parse(tokens, null);
                stack.push(expression);
                return parse(tokens, stack);
            }
            case "tolower": {
                IdEqIgnoreCase expression = IdEqIgnoreCase.parse(tokens);
                checkForAndExpression(stack, expression);
                return parse(tokens, stack);
            }
            case "or": {
                OrExpression expression = new OrExpression();
                Expression secondExpression = parse(tokens, null);
                String nextToken = tokens.peek();
                if (nextToken != null && nextToken.equals("and")) {
                    stack.push(secondExpression);
                    secondExpression = parse(tokens, stack);
                }
                expression.secondExpression = secondExpression;
                expression.firstExpression = stack.pop();
                return expression;
            }

            case ")": {
                return stack.pop();
            }

            case "and": {
                AndExpression expression = new AndExpression();
                expression.firstExpression = stack.pop();
                stack.push(expression);
                return parse(tokens, stack);
            }

            case "isLatestVersion": {
                Expression expression = new LatestVersionExpression();
                checkForAndExpression(stack, expression);
                return parse(tokens, stack);
            }
            default:
                throw new NugetFormatException("Токен не поддерживается");
        }
    }

    protected Expression parse(String value) throws NugetFormatException {
        Queue<String> tokenQueue = new ArrayDeque<>(split(value));
        return parse(tokenQueue, null);
    }
}
