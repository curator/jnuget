package ru.aristar.jnuget.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

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

    public <T> T getPackages(Class<T> responseType, String packages, String $orderby, String $filter, String searchTerm, String $top, String targetFramework, String $skip) throws UniformInterfaceException {
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
        resource = resource.path(java.text.MessageFormat.format("nuget/{0}", new Object[]{packages}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T getPackage(Class<T> responseType, String id, String version) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("download/{0}/{1}", new Object[]{id, version}));
        return resource.get(responseType);
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

    public <T> T getMetadata(Class<T> responseType, String metadata) throws UniformInterfaceException {
        WebResource resource = webResource;
        resource = resource.path(java.text.MessageFormat.format("nuget/{0}", new Object[]{metadata}));
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public void close() {
        client.destroy();
    }
}
