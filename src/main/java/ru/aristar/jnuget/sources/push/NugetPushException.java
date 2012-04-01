package ru.aristar.jnuget.sources.push;

/**
 * Ошибка помещения пакета в хранилище.
 *
 * @author sviridov
 */
public class NugetPushException extends Exception {

    public NugetPushException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NugetPushException(Throwable cause) {
        super(cause);
    }

    public NugetPushException(String message, Throwable cause) {
        super(message, cause);
    }

    public NugetPushException(String message) {
        super(message);
    }

    public NugetPushException() {
    }
}
