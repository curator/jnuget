package ru.aristar.jnuget;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.rss.NuPkgToRssTransformer;
import ru.aristar.jnuget.rss.PackageEntry;

/**
 *
 * @author sviridov
 */
public class NugetContext {

    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Коневой URI приложения
     */
    private final URI rootUri;

    /**
     * @param rootUri коневой URI приложения
     */
    public NugetContext(URI rootUri) {
        this.rootUri = rootUri;
        logger.debug("Создан контекст для приложения {}", new Object[]{rootUri});
    }

    /**
     * @return коневой URI приложения
     */
    public URI getRootUri() {
        return rootUri;
    }

    /**
     * Создает RSS вложение с информацией о пакете (дополняет его информацией от
     * сервера)
     *
     * @param nupkgFile пакет
     * @return RSS вложение
     * @throws NoSuchAlgorithmException ошибка вычисления HASH
     * @throws IOException ошибка чтения пакета
     * @throws NugetFormatException некорректная спецификация пакета
     */
    public PackageEntry createPackageEntry(Nupkg nupkgFile) throws NoSuchAlgorithmException, IOException, NugetFormatException {
        return new ContextPackageEntry(nupkgFile);
    }

    /**
     * Создает преобразователь пакетов в RSS ленту
     *
     * @return преобразователь пакетов в RSS ленту
     */
    public NuPkgToRssTransformer createToRssTransformer() {
        return new ContextNuPkgToRssTransformer();
    }

    /**
     * Преобразователь в RSS, содержащий контекст
     */
    private class ContextNuPkgToRssTransformer extends NuPkgToRssTransformer {

        @Override
        protected NugetContext getContext() {
            return NugetContext.this;
        }
    }

    /**
     * RSS вложение, содержащее контекст
     */
    private class ContextPackageEntry extends PackageEntry {

        /**
         * @param nupkgFile пакет на основе кторого создается RSS вложение
         * @throws NoSuchAlgorithmException не указан алгоритм вычисления HASH
         * @throws IOException ошибка чтения пакета
         * @throws NugetFormatException некорректная спецификация пакета
         */
        public ContextPackageEntry(Nupkg nupkgFile) throws NoSuchAlgorithmException, IOException, NugetFormatException {
            super(nupkgFile);
        }

        @Override
        protected String getRootUri() {
            return NugetContext.this.rootUri.toString();
        }
    }
}
