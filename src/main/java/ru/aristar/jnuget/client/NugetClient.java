package ru.aristar.jnuget.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import static java.text.MessageFormat.format;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.MainUrlResource;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.common.ProxyOptions;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.query.AndExpression;
import ru.aristar.jnuget.query.IdEqIgnoreCase;
import ru.aristar.jnuget.query.VersionEq;
import ru.aristar.jnuget.rss.PackageFeed;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 *
 * @author sviridov
 */
public class NugetClient implements AutoCloseable {

    /**
     * Ресурс, к которому осуществляется подключение REST клиента
     */
    private WebResource webResource;
    /**
     * REST клиент
     */
    private Client client;
    /**
     * URL хранилища по умолчанию
     */
    private static final String DEFAULT_REMOTE_STORAGE_URL = "http://localhost:8080/resources";
    /**
     * Ключ доступа к удаленному хранилищу
     */
    private String apiKey;
    /**
     * Логгер
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Конструктор по умолчанию
     */
    public NugetClient() {
        final ProxyOptions proxyOptions = PackageSourceFactory.getInstance().getOptions().getProxyOptions();
        ClientConfig config = createClientConfig(proxyOptions);
        client = ApacheHttpClient4.create(config);
        client.setFollowRedirects(Boolean.TRUE);
        client.addFilter(new GZIPContentEncodingFilter());
        webResource = client.resource(DEFAULT_REMOTE_STORAGE_URL);
    }

    /**
     * @param url URL ресурса, к которому необходимо подключиться
     */
    public NugetClient(String url) {
        this();
        NugetClient.this.setUrl(url);
    }

    /**
     * @param url URL удаленного хранилища
     */
    public void setUrl(String url) {
        webResource = client.resource(url);
    }

    /**
     * @return URL удаленного хранилища
     */
    public String getUrl() {
        return webResource == null ? null : webResource.getURI().toString();
    }

    /**
     * @return ключ доступа к удаленному хранилищу
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey ключ доступа к удаленному хранилищу
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Возвращает список пакетов
     *
     * @param filter параметр фильтрации
     * @param searchTerm условие поиска
     * @param top количество запрашиваемых пакетов
     * @param targetFramework фреймворк, для которого собраны пакеты
     * @param skip пропустить пакетов
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public PackageFeed getPackages(String filter, String searchTerm,
            Integer top, String targetFramework, Integer skip)
            throws IOException, URISyntaxException {
        Map<String, String> params = new HashMap<>(6);
        params.put("$orderby", "Id");
        params.put("$filter", filter);
        params.put("searchTerm", searchTerm);
        params.put("$top", top == null ? null : top.toString());
        params.put("targetFramework", targetFramework);
        params.put("$skip", skip == null ? null : skip.toString());

        MediaType[] accept = {MediaType.TEXT_HTML_TYPE,
            MediaType.APPLICATION_XHTML_XML_TYPE,
            MediaType.APPLICATION_XML_TYPE,
            MediaType.WILDCARD_TYPE};

        PackageFeed feed = null;
        int tryCount = 0;
        final URI storageURI = webResource.getURI();
        do {
            try {
                logger.debug("Получение пакетов из {} Top: {}, Skip: {}, попытка {}",
                        new Object[]{storageURI, top, skip, tryCount + 1});
                feed = get(client, storageURI, "Packages", params, accept, PackageFeed.class);
            } catch (IOException e) {
                logger.warn("Не удалось получить пакеты для хранилища {} "
                        + "Top: {} Skip: {} причина: {} попытка {}",
                        new Object[]{storageURI, top, skip, e.getMessage(), tryCount + 1});
                tryCount = tryCount + 1;
            }
        } while (feed == null && tryCount < MAX_TRY_COUNT);
        return feed;
    }

    /**
     * Получает пакет из удаленного хранилища NuGet
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return пакет
     * @throws IOException ошибка чтения потока с телом пакета
     * @throws URISyntaxException
     * @throws NugetFormatException ошибка вычисления HASH пакета
     */
    public TempNupkgFile getPackage(String id, Version version) throws IOException, URISyntaxException, NugetFormatException {
        IdEqIgnoreCase eqIgnoreCase = new IdEqIgnoreCase(id);
        VersionEq versionEq = new VersionEq(version);
        AndExpression andExpression = new AndExpression(eqIgnoreCase, versionEq);
        String filter = andExpression.toString();
        PackageFeed feed = getPackages(filter, null, 100, null, 0);
        if(feed.getEntries().isEmpty()) {
            return null;
        }
        URI uri = URI.create(feed.getEntries().get(0).getContent().getSrc());

        try (InputStream inputStream = get(client, uri, "", InputStream.class)) {
            TempNupkgFile nupkgFile = new TempNupkgFile(inputStream);
            return nupkgFile;
        }
    }

    /**
     * Возвращает количество пакетов в удаленном хранилище
     *
     * @param isLatestVersion только последние версии пакетов
     * @return количество пакетов
     * @throws IOException ошибка чтения пакетов из удаленного репозитория
     * @throws URISyntaxException ошибка URI
     */
    public int getPackageCount(final boolean isLatestVersion) throws IOException, URISyntaxException {
        URI uri = webResource.getURI();
        final String path = "Packages/$count";
        final MediaType[] mediaType = new MediaType[]{MediaType.TEXT_PLAIN_TYPE};
        if (isLatestVersion) {
            Map<String, String> params = new HashMap<>(1);
            params.put("$filter", "IsLatestVersion");
            final String response = get(client, uri, path, params, mediaType, String.class);
            return Integer.parseInt(response);
        }
        final String response = get(client, uri, path, null, mediaType, String.class);
        return Integer.parseInt(response);
    }

    /**
     * Отправляет пакет на сервер
     *
     * @param nupkg пакет
     * @return ответ сервера
     * @throws UniformInterfaceException
     * @throws IOException ошибка чтения локального пакета
     */
    public ClientResponse putPackage(Nupkg nupkg)
            throws UniformInterfaceException, IOException {
        webResource.header(MainUrlResource.API_KEY_HEADER_NAME, apiKey);
        return webResource.put(ClientResponse.class, nupkg.getStream());
    }

    @Override
    public void close() {
        client.destroy();
    }

    /**
     * Получить класс указанного типа с URI
     *
     * @param <T> тип
     * @param client клиент
     * @param uri URI ресурса
     * @param targetClass класс, который необходимо получить
     * @param querryParams параметры запроса
     * @param accept поддерживаемые типы
     * @param path относительный путь к объекту
     * @return объект из удаленного URI
     * @throws IOException ошибка чтения из сокета
     * @throws URISyntaxException URI имеет некорректный синтаксис
     */
    private <T> T get(Client client, URI uri, String path,
            Map<String, String> querryParams, MediaType[] accept, Class<T> targetClass)
            throws IOException, URISyntaxException {
        WebResource currentResource = client.resource(uri);
        //Относительный путь
        if (path != null) {
            currentResource = currentResource.path(path);
        }
        //Параметры запроса
        if (querryParams != null) {
            for (Map.Entry<String, String> entry : querryParams.entrySet()) {
                if (entry.getValue() != null) {
                    currentResource = currentResource.queryParam(entry.getKey(), entry.getValue());
                }
            }
        }
        try {
            ClientResponse response;
            //Заголовки запроса
            if (accept != null && accept.length != 0) {
                response = currentResource.accept(accept).get(ClientResponse.class);
            } else {
                response = currentResource.get(ClientResponse.class);
            }
            switch (response.getClientResponseStatus()) {
                case ACCEPTED:
                case OK: {
                    logger.trace("Получен ответ для типа {}", new Object[]{targetClass.getName()});
                    final T result = response.getEntity(targetClass);
                    if (!Closeable.class.isAssignableFrom(targetClass)) {
                        logger.trace("Принудительное закрытие потока от сервера.");
                        response.getEntityInputStream().close();
                    }
                    return result;
                }
                case FOUND:
                case MOVED_PERMANENTLY: {
                    String redirectUriString = response.getHeaders().get("Location").get(0);
                    URI redirectUri = new URI(redirectUriString);
                    response.getEntityInputStream().close();
                    return get(client, redirectUri, null, querryParams, accept, targetClass);
                }
                case BAD_REQUEST: {
                    response.getEntityInputStream().close();
                    return get(client, uri, null, querryParams, accept, targetClass);
                }
                case INTERNAL_SERVER_ERROR: {
                    response.getEntityInputStream().close();
                    throw new IOException("Ошибка на удаленном сервере код: "
                            + response.getClientResponseStatus().getStatusCode() + " "
                            + response.getClientResponseStatus().getReasonPhrase());
                }
                default:
                    throw new IOException("Статус сообщения " + response.getClientResponseStatus() + " не поддерживается");
            }
        } catch (ClientHandlerException e) {
            logger.warn(format("Ошибка получения данных с удаленного сервера по адресу {0}", currentResource.getURI()), e);
            throw e;
        }
    }

    /**
     * Получить класс указанного типа с URI
     *
     * @param <T> тип
     * @param client клиент
     * @param uri URI ресурса
     * @param targetClass класс, который необходимо получить
     * @param path относительный путь к объекту
     * @return объект из удаленного URI
     * @throws IOException ошибка чтения из сокета
     * @throws URISyntaxException URI имеет некорректный синтаксис
     */
    private <T> T get(Client client, URI uri, String path, Class<T> targetClass)
            throws IOException, URISyntaxException {
        return get(client, uri, path, null, null, targetClass);
    }

    /**
     * Создает настройки подключения к серверу NuGet
     *
     * @param proxyOptions настройки прокси
     * @return
     */
    private ClientConfig createClientConfig(ProxyOptions proxyOptions) {
        ClientConfig config = new DefaultClientConfig();
        if (proxyOptions.getUseSystemProxy() != null && proxyOptions.getUseSystemProxy()) {
            logger.info("Используется системный прокси");
            System.setProperty("java.net.useSystemProxies", "true");
        } else if (proxyOptions.getNoProxy() != null && proxyOptions.getNoProxy()) {
            logger.info("Прокси отключен");
            throw new UnsupportedOperationException("Отключение прокси не реализовано");
        } else {
            logger.info("Используется прокси {}:{}",
                    new Object[]{proxyOptions.getHost(), proxyOptions.getPort()});
            String host = proxyOptions.getHost();
            if (!host.toLowerCase().startsWith("http://")) {
                host = "http://" + host;
            }
            URI proxyUri = URI.create(host + ":" + proxyOptions.getPort());
            config.getProperties().put(ApacheHttpClient4Config.PROPERTY_PROXY_URI, proxyUri);
            config.getProperties().put(ApacheHttpClient4Config.PROPERTY_PROXY_USERNAME, proxyOptions.getLogin());
            config.getProperties().put(ApacheHttpClient4Config.PROPERTY_PROXY_PASSWORD, proxyOptions.getPassword());
        }
        return config;
    }

    /**
     * Получить класс указанного типа с URI
     *
     * @param <T> тип
     * @param uri URI ресурса
     * @param targetClass класс, который необходимо получить
     * @return объект из удаленного URI
     * @throws IOException ошибка чтения из сокета
     * @throws URISyntaxException URI имеет некорректный синтаксис
     */
    public <T> T get(URI uri, Class<T> targetClass)
            throws IOException, URISyntaxException {
        return get(client, uri, null, null, null, targetClass);
    }
    /**
     * Максимально допустимое число попыток получения информации из хранилища
     */
    public static final int MAX_TRY_COUNT = 3;
}
