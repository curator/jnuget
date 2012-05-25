package ru.aristar.jnuget.query;

import java.util.Queue;
import java.util.Stack;
import ru.aristar.jnuget.files.NugetFormatException;

/**
 * Лексический анализатор запросов
 *
 * @author sviridov
 */
public class QueryLexer {

    /**
     * Проверяет наличие в стеке предыдущей операции и если это операция AND
     * изменяет ее приоритет
     *
     * @param stack стек с выражениями
     * @param expression последнее распознанное выражение
     */
    private void checkForAndExpression(Stack<Expression> stack, Expression expression) {
        if (!stack.isEmpty() && stack.peek() instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) stack.pop();
            andExpression.setSecondExpression(expression);
            stack.push(andExpression);
        } else {
            stack.push(expression);
        }
    }

    /**
     * Создает дерево выражений, на основе очереди токенов и стека ранее
     * распознаных выражений
     *
     * @param tokens очередь токенов
     * @param stack стека ранее распознаных выражений
     * @return дерево выражений
     * @throws NugetFormatException токен не соответствует формату
     */
    protected Expression parse(Queue<String> tokens, Stack<Expression> stack) throws NugetFormatException {
        if (tokens.isEmpty()) {
            return stack.pop();
        }
        String token = tokens.poll();

        if (stack == null) {
            stack = new Stack<>();
        }
        switch (token.toLowerCase()) {
            case "(": {
                Expression expression = parse(tokens, null);
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
                expression.setSecondExpression(secondExpression);
                expression.setFirstExpression(stack.pop());
                return expression;
            }

            case ")": {
                return stack.pop();
            }

            case "and": {
                AndExpression expression = new AndExpression();
                expression.setFirstExpression(stack.pop());
                stack.push(expression);
                return parse(tokens, stack);
            }

            case "islatestversion": {
                Expression expression = new LatestVersionExpression();
                checkForAndExpression(stack, expression);
                return parse(tokens, stack);
            }
            default:
                throw new NugetFormatException("Токен не поддерживается");
        }
    }

    /**
     * Создает дерево выражений на основе строки токенов
     *
     * @param value строка с ключевыми словами
     * @return дерево выражений
     * @throws NugetFormatException ключевое слово не соответствует формату
     */
    public Expression parse(String value) throws NugetFormatException {
        TokenQueue tokenQueue = new TokenQueue(value);
        try {
            return parse(tokenQueue, null);
        } catch (NugetFormatException e) {
            throw new NugetFormatException("Не удалось проанализировать "
                    + "строку: \"" + value + "\", позиция "
                    + tokenQueue.getCurrentPosition(), e);
        }
    }
}
