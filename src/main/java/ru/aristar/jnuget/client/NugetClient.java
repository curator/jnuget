package ru.aristar.jnuget.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.NuspecFile;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.rss.PackageEntry;
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
public class NugetClient {

    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/resources";

    public NugetClient() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(BASE_URI);
    }

    public void setUrl(String url) {
        webResource = client.resource(url);
    }

    public String getUrl() {
        return webResource == null ? null : webResource.getURI().toString();
    }

    public ClientResponse postPackageMetadata(Object requestEntity) throws UniformInterfaceException {
        return webResource.path("PublishedPackages/Publish").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(ClientResponse.class, requestEntity);
    }

    public PackageFeed getPackages(String filter, String searchTerm, String top, String targetFramework, String skip) throws UniformInterfaceException, NugetFormatException {
        WebResource resource = webResource;
        resource = resource.queryParam("$orderby", "id");
        if (filter != null) {
            resource = resource.queryParam("$filter", filter);
        }
        if (searchTerm != null) {
            resource = resource.queryParam("searchTerm", searchTerm);
        }
        if (top != null) {
            resource = resource.queryParam("$top", top);
        }
        if (targetFramework != null) {
            resource = resource.queryParam("targetFramework", targetFramework);
        }
        if (skip != null) {
            resource = resource.queryParam("$skip", skip);
        }
        resource = resource.path("nuget/Packages");
        PackageFeed feed = resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(PackageFeed.class);
        ArrayList<Nupkg> result = new ArrayList<>();
        for (PackageEntry entry : feed.getEntries()) {
            NuspecFile nuspecFile = new NuspecFile(entry);
            result.add(null);
        }
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

    public ClientResponse putPackage() throws UniformInterfaceException {
        return webResource.put(ClientResponse.class);
    }

    public <T> T getRootXml(Class<T> responseType) throws UniformInterfaceException {
        WebResource resource = webResource;
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
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

    public void close() {
        client.destroy();
    }
}
