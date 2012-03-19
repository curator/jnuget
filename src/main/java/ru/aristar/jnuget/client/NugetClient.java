package ru.aristar.jnuget.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.HttpHeaders;
import ru.aristar.jnuget.MainUrlResource;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.rss.PackageFeed;

/**
 * Jersey REST client generated for REST resource:MainUrlResource<br> USAGE:
 * <pre>
 *        NugetClient client = new NugetClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author sviridov
 */
public class NugetClient implements AutoCloseable {

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
     * @throws UniformInterfaceException
     * @throws NugetFormatException
     */
    public PackageFeed getPackages(String filter, String searchTerm, Integer top, String targetFramework, Integer skip) throws UniformInterfaceException, NugetFormatException {
        WebResource resource = webResource;
        resource = resource.queryParam("$orderby", "Id");
        if (filter != null) {
            resource = resource.queryParam("$filter", filter);
        }
        if (searchTerm != null) {
            resource = resource.queryParam("searchTerm", searchTerm);
        }
        if (top != null) {
            resource = resource.queryParam("$top", top.toString());
        }
        if (targetFramework != null) {
            resource = resource.queryParam("targetFramework", targetFramework);
        }
        if (skip != null) {
            resource = resource.queryParam("$skip", skip.toString());
        }
        resource = resource.path("Packages");
        Builder builder = resource.header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        PackageFeed feed = builder.get(PackageFeed.class);
        return feed;
    }

    /**
     * Получает пакет из удаленного хранилища NuGet
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return пакет
     * @throws Exception ошибка получения пакета
     */
    public TempNupkgFile getPackage(String id, Version version) throws Exception {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("download/{0}/{1}", new Object[]{id, version.toString()}));
        InputStream inputStream = resource.get(InputStream.class);
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

    public ClientResponse postPackage(String apiKey) throws UniformInterfaceException {
        return webResource.path(java.text.MessageFormat.format("PackageFiles/{0}/nupkg", new Object[]{apiKey})).post(ClientResponse.class);
    }

    public <T> T getPackageCount(Class<T> responseType, String packages, String count, String $orderby, String $filter, String searchTerm, String $top, String targetFramework, String $skip) throws UniformInterfaceException {
        WebResource resource = webResource;
        if ($orderby != null) {
            resource = resource.queryParam("$orderby", $orderby);
        }
        if ($filter != null) {
            resource = resource.queryParam("$filter", $filter);
        }
        if (searchTerm != null) {
            resource = resource.queryParam("searchTerm", searchTerm);
        }
        if ($top != null) {
            resource = resource.queryParam("$top", $top);
        }
        if (targetFramework != null) {
            resource = resource.queryParam("targetFramework", targetFramework);
        }
        if ($skip != null) {
            resource = resource.queryParam("$skip", $skip);
        }
        resource = resource.path(java.text.MessageFormat.format("nuget/{0}/{1}", new Object[]{packages, count}));
        return resource.accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(responseType);
    }

    @Override
    public void close() {
        client.destroy();
    }
}
