package ru.aristar.common;

import java.util.Collection;
import static org.junit.Assert.fail;

/**
 *
 * @author sviridov
 */
public class TestHelper {

    public static void assertOneOfAreEquals(String message,
            Object expected,
            Collection<? extends Object> actuals) {
        for (Object actual : actuals) {
            if (expected.equals(actual)) {
                return;
            }
        }
        fail(message + " " + expected.toString() + " not found");
    }
}
