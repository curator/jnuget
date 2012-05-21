package ru.aristar.integration;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.rss.PackageFeed;

/**
 * Проверка редиректа для клиента JNuGet
 *
 * @author sviridov
 */
public class NugetClientRedirectTest {

    /**
     * Создание тестового HTTP сервера
     *
     * @param cahinElements последовательность "ответов от сервера"
     * @return HTTP сервер
     * @throws IOException ошибка открытия сокета
     */
    private HttpServer createHttpServer(ServerResponse... cahinElements) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(1234), 0);
        TestHttpHandler serv = new TestHttpHandler(cahinElements);
        server.createContext("/", serv);
        server.setExecutor(null); // creates a default executor
        return server;
    }

    /**
     * Ответ от сервера
     */
    private class ServerResponse {

        /**
         * Тело ответа
         */
        private final InputStream sourceStream;
        /**
         * Код ошибки HTTP
         */
        private final ClientResponse.Status responseCode;
        /**
         * Заголовки HTTP ответа
         */
        private final String[][] headers;

        /**
         * @param sourceStream тело ответа
         * @param responseCode код ошибки HTTP
         * @param headers заголовки HTTP ответа
         */
        public ServerResponse(InputStream sourceStream, ClientResponse.Status responseCode, String[][] headers) {
            this.sourceStream = sourceStream;
            this.responseCode = responseCode;
            this.headers = headers;
        }

        /**
         * Основной метод обработки запроса
         *
         * @param exchange запрос/ответ
         * @throws IOException ошибка отправки ответа или чтения тела ответа
         */
        public void processResponse(HttpExchange exchange) throws IOException {
            for (int i = 0; i < headers.length; i++) {
                exchange.getResponseHeaders().put(headers[i][0], Arrays.asList(headers[i][1]));
            }
            exchange.sendResponseHeaders(responseCode.getStatusCode(), 0);
            if (sourceStream != null) {
                ReadableByteChannel sourceChanel = Channels.newChannel(sourceStream);
                WritableByteChannel targetChannel = Channels.newChannel(exchange.getResponseBody());
                TempNupkgFile.fastChannelCopy(sourceChanel, targetChannel);
            }
            exchange.close();
        }
    }

    /**
     * Тестовый обработчик запросов к HTTP серверу
     */
    private class TestHttpHandler implements HttpHandler {

        /**
         * Ответы от сервера
         */
        private final ServerResponse[] serverResponses;
        /**
         * Текущий ответ от сервера
         */
        private int currPos = 0;

        /**
         * @param chainElements ответы от сервера
         */
        public TestHttpHandler(ServerResponse[] chainElements) {
            this.serverResponses = chainElements;
        }

        /**
         * @param exchange запрос/ответ
         * @throws IOException ошибка обработки запроса
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (currPos >= serverResponses.length) {
                currPos = 0;
            }
            serverResponses[currPos].processResponse(exchange);
            currPos++;
        }
    }

    /**
     * Проверка получения RSS с информацией о пакетах если первые два вызова -
     * REDIRECT
     *
     * @throws IOException ошибка создания веб сервера
     * @throws UniformInterfaceException ошибка чтения из сокета
     * @throws NugetFormatException тело ответа от сервера не соответствует
     * формату NuGet
     */
    @Test
    public void testGetPackageListWithRedirect()
            throws IOException, UniformInterfaceException, NugetFormatException, URISyntaxException {
        //GIVEN
        ServerResponse c1 = new ServerResponse(
                null,
                ClientResponse.Status.MOVED_PERMANENTLY,
                new String[][]{
                    new String[]{"Location", "http://localhost:1234/2/"}
                });
        ServerResponse c2 = new ServerResponse(
                null,
                ClientResponse.Status.MOVED_PERMANENTLY,
                new String[][]{
                    new String[]{"Location", "http://localhost:1234/3/"}
                });
        ServerResponse c3 = new ServerResponse(
                this.getClass().getResourceAsStream("/rss/rss_feed.xml"),
                ClientResponse.Status.OK,
                new String[][]{
                    new String[]{"Content-Type", "application/xml"}
                });
        HttpServer server = createHttpServer(c1, c2, c3);
        server.start();
        NugetClient nugetClient = new NugetClient();
        nugetClient.setUrl("http://localhost:1234");
        //WHEN
        PackageFeed result = nugetClient.getPackages(null, null, 100, null, 0);
        server.stop(0);
        //THEN
        assertThat(result.getEntries().size(), is(equalTo(26)));
    }

    /**
     * Проверка получения RSS с информацией о пакетах если первый вызов -
     * REDIRECT
     *
     * @throws IOException ошибка создания веб сервера
     * @throws NugetFormatException тело ответа от сервера не соответствует
     * формату NuGet
     */
    @Test
    public void testGetPackageBodyListWithRedirect() throws IOException, NugetFormatException, URISyntaxException {
        //GIVEN
        ServerResponse c1 = new ServerResponse(
                null,
                ClientResponse.Status.MOVED_PERMANENTLY,
                new String[][]{
                    new String[]{"Location", "http://localhost:1234/2/"}
                });
        ServerResponse c2 = new ServerResponse(
                this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg"),
                ClientResponse.Status.OK,
                new String[][]{
                    new String[]{"Content-Type", "application/xml"}
                });
        HttpServer server = createHttpServer(c1, c2);
        server.start();
        NugetClient nugetClient = new NugetClient();
        nugetClient.setUrl("http://localhost:1234");
        //WHEN
        TempNupkgFile result = nugetClient.getPackage("NUnit", Version.parse("2.5.9.10348"));
        server.stop(0);
        //THEN
        assertThat(result.getId(), is(equalTo("NUnit")));
        assertThat(result.getVersion(), is(equalTo(Version.parse("2.5.9.10348"))));
    }
}
