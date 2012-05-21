package ru.aristar.jnuget.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import ru.aristar.jnuget.MainUrlResource;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.rss.PackageFeed;

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
     * Конструктор по умолчанию
     */
    public NugetClient() {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        client.setFollowRedirects(Boolean.TRUE);
        client.addFilter(new GZIPContentEncodingFilter());
        webResource = client.resource(DEFAULT_REMOTE_STORAGE_URL);
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

        PackageFeed feed = get(client, webResource.getURI(), "Packages", params, accept, PackageFeed.class);
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
        URI uri = webResource.getURI();
        final String path = java.text.MessageFormat.format("download/{0}/{1}", new Object[]{id, version.toString()});
        InputStream inputStream = get(client, uri, path, InputStream.class);
        TempNupkgFile nupkgFile = new TempNupkgFile(inputStream);
        return nupkgFile;
    }

    /**
     * Отправляет пакет на сервер
     *
     * @param nupkg пакет
     * @param apiKey ключ доступа (пароль)
     * @return ответ сервера
     * @throws UniformInterfaceException
     * @throws IOException ошибка чтения локального пакета
     */
    public ClientResponse putPackage(Nupkg nupkg, String apiKey)
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
    public static <T> T get(Client client, URI uri, String path,
            Map<String, String> querryParams, MediaType[] accept, Class<T> targetClass)
            throws IOException, URISyntaxException {
        WebResource webResource = client.resource(uri);
        //Относительный путь
        if (path != null) {
            webResource = webResource.path(path);
        }
        //Параметры запроса
        if (querryParams != null) {
            for (Map.Entry<String, String> entry : querryParams.entrySet()) {
                if (entry.getValue() != null) {
                    webResource = webResource.queryParam(entry.getKey(), entry.getValue());
                }
            }
        }
        ClientResponse response;
        //Заголовки запроса
        if (accept != null && accept.length != 0) {
            response = webResource.accept(accept).get(ClientResponse.class);
        } else {
            response = webResource.get(ClientResponse.class);
        }
        switch (response.getClientResponseStatus()) {
            case ACCEPTED:
            case OK: {
                return response.getEntity(targetClass);
            }
            case FOUND:
            case MOVED_PERMANENTLY: {
                String redirectUriString = response.getHeaders().get("Location").get(0);
                URI redirectUri = new URI(redirectUriString);
                return get(client, redirectUri, null, querryParams, accept, targetClass);
            }
            default:
                throw new IOException("Статус сообщения " + response.getClientResponseStatus() + " не поддерживается");
        }
    }

    /**
     * Получить класс указанного типа с URI
     *
     * @param <T> тип
     * @param client клиент
     * @param uri URI ресурса
     * @param targetClass класс, который необходимо получить
     * @param querryParams параметры запроса
     * @param path относительный путь к объекту
     * @return объект из удаленного URI
     * @throws IOException ошибка чтения из сокета
     * @throws URISyntaxException URI имеет некорректный синтаксис
     */
    public static <T> T get(Client client, URI uri, String path, Map<String, String> querryParams, Class<T> targetClass)
            throws IOException, URISyntaxException {
        return get(client, uri, path, querryParams, null, targetClass);
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
    public static <T> T get(Client client, URI uri, String path, Class<T> targetClass)
            throws IOException, URISyntaxException {
        return get(client, uri, path, null, null, targetClass);
    }

    /**
     * Получить класс указанного типа с URI
     *
     * @param <T> тип
     * @param client клиент
     * @param uri URI ресурса
     * @param targetClass класс, который необходимо получить
     * @return объект из удаленного URI
     * @throws IOException ошибка чтения из сокета
     * @throws URISyntaxException URI имеет некорректный синтаксис
     */
    public static <T> T get(Client client, URI uri, Class<T> targetClass)
            throws IOException, URISyntaxException {
        return get(client, uri, null, null, null, targetClass);
    }
}
