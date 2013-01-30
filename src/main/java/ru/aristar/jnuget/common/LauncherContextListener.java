package ru.aristar.jnuget.common;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * @author sviridov
 */
public class LauncherContextListener implements ServletContextListener {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        try {
            logger.info("Инициализация сервера JNuGet");
            URL url = Thread.currentThread().getContextClassLoader().getResource("ru/aristar/jnuget/security/jaas.config");
            File file = new File(url.toURI());
            System.setProperty("java.security.auth.login.config", file.getAbsolutePath());
            PackageSourceFactory.getInstance().getPackageSources();
        } catch (URISyntaxException e) {
            logger.error("Ошибка инициализации сервлета", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
    }
}
