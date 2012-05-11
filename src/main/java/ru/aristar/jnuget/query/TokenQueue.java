package ru.aristar.jnuget.query;

import java.util.*;

/**
 *
 * @author sviridov
 */
public class TokenQueue implements Queue<String> {

    /**
     * Массив исходных символов
     */
    private final char[] querryChars;
    /**
     * Последний извлеченный токен
     */
    private String lastToken;
    /**
     * Итератор по токенам
     */
    private final TokenIterator iterator;

    /**
     * Итератор по токенам строки
     */
    public class TokenIterator implements Iterator<String> {

        /**
         * Текущая позиция в итераторе
         */
        private int currPos = 0;

        @Override
        public boolean hasNext() {
            return currPos < querryChars.length;
        }

        @Override
        public String next() {
            StringBuilder builder = new StringBuilder();
            while (currPos < querryChars.length) {
                char c = querryChars[currPos];
                String tokenString = new String(new char[]{c});
                if (isSkipToken(tokenString) && builder.length() == 0) {
                    currPos++;
                    continue;
                }
                if (isTokenDone(builder, tokenString)) {
                    return builder.toString();
                }
                if (!isSkipToken(tokenString)) {
                    builder.append(tokenString);
                    if (currPos == querryChars.length - 1) {
                        currPos++;
                        return builder.toString();
                    }
                }
                currPos++;
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * @param string проверяемая строка
     * @return является ли строка "пустым" токеном
     */
    private boolean isSkipToken(String string) {
        return string.matches("[\\s\\r\\n]+");
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
     * @param querryString строка, подлежащая разделению на токены
     */
    public TokenQueue(String querryString) {
        this.querryChars = querryString.toCharArray();
        iterator = new TokenIterator();
    }

    @Override
    public boolean add(String e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean offer(String e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String poll() {
        if (lastToken != null) {
            String result = lastToken;
            lastToken = null;
            return result;
        }
        return iterator.next();
    }

    @Override
    public String element() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String peek() {
        if (lastToken == null) {
            lastToken = iterator.next();
        }
        return lastToken;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<String> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] toArray() {
        TokenIterator tokenIterator = new TokenIterator();
        ArrayList<String> arrayList = new ArrayList<>();
        while (tokenIterator.hasNext()) {
            arrayList.add(tokenIterator.next());
        }
        return arrayList.toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (String.class.isAssignableFrom(a.getClass())) {
            throw new ClassCastException("невозможно преобразовать массив String к массиву" + a.getClass());
        }
        Object[] resArray = toArray();

        if (a.length < resArray.length) {
            return (T[]) Arrays.copyOf(resArray, resArray.length, a.getClass());
        }
        System.arraycopy(resArray, 0, a, 0, resArray.length);
        if (a.length > resArray.length) {
            a[resArray.length] = null;
        }
        return a;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
