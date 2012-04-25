package ru.aristar.jnuget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import ru.aristar.jnuget.files.Nupkg;

/**
 * Лексический анализатор запросов
 *
 * @author sviridov
 */
public class QueryLexer {

    public enum Operation {

        AND,
        OR,
        EQ
    }

    public static interface Expression {

        public Operation getOperation();

        public List<Nupkg> execute();
    }

    public static class GroupExpression implements Expression {

        public Expression innerExpression;

        @Override
        public Operation getOperation() {
            return null;
        }

        @Override
        public List<Nupkg> execute() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static class IdEqIgnoreCase implements Expression {

        public String value;

        @Override
        public Operation getOperation() {
            return Operation.EQ;
        }

        public List<Nupkg> execute() {
            return null;
        }
    }

    public static class OrExpression implements Expression {

        public Expression firstExpression;
        public Expression secondExpression;

        @Override
        public Operation getOperation() {
            return Operation.OR;
        }

        @Override
        public List<Nupkg> execute() {
            throw new UnsupportedOperationException("Not supported yet.");
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

    protected Expression parse(Iterator<String> iterator, Stack<Expression> stack) {
        if (!iterator.hasNext()) {
            return stack.pop();
        }
        String token = iterator.next();

        if (stack == null) {
            stack = new Stack<>();
        }
        switch (token) {
            case "(": {
                GroupExpression expression = new GroupExpression();
                expression.innerExpression = parse(iterator, null);
                stack.push(expression);
                return parse(iterator, stack);
            }
            case "tolower": {
                iterator.next(); //(
                iterator.next(); //id
                iterator.next(); //)
                iterator.next(); //eq
                iterator.next(); //'
                IdEqIgnoreCase expression = new IdEqIgnoreCase();
                expression.value = iterator.next();
                iterator.next(); //'
                stack.push(expression);
                return parse(iterator, stack);
            }
            case "or": {
                OrExpression expression = new OrExpression();
                expression.firstExpression = stack.pop();
                expression.secondExpression = parse(iterator, null);
                return expression;
            }

            case ")": {
                return stack.pop();
            }
            default:
                throw new UnsupportedOperationException("Токен не поддерживается");
        }
    }

    protected Expression parse(String value) {
        List<String> tokens = split(value);
        Iterator<String> iterator = tokens.iterator();
        return parse(iterator, null);
    }
}
