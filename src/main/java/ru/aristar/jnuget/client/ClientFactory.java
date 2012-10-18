package ru.aristar.jnuget.client;

/**
 *
 * @author sviridov
 */
public class ClientFactory {

    private final String url;

    public ClientFactory(String url) {
        this.url = url;
    }

    public NugetClient createClient() {
        return new NugetClient(url);
    }
}
