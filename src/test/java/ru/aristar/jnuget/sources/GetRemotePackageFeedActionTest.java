package ru.aristar.jnuget.sources;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.client.ClientFactory;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.RemoteNupkg;
import ru.aristar.jnuget.rss.PackageEntry;
import ru.aristar.jnuget.rss.PackageFeed;

/**
 *
 * @author sviridov
 */
public class GetRemotePackageFeedActionTest {

    /**
     * Контекст тестовых заглушек
     */
    private Mockery context = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
        }
    };

    /**
     * Проверка получения списка пакетов из удаленного хранилища
     *
     * @throws IOException ошибка чтения из удаленного хранилища
     * @throws URISyntaxException ошибка в синтаксисе URI хранилища
     * @throws NugetFormatException некорректный формат тестовой версии
     */
    @Test
    public void testCompute() throws IOException, URISyntaxException, NugetFormatException {
        //GIVEN
        List<RemoteNupkg> arrayList = new ArrayList<>();
        arrayList = Collections.synchronizedList(arrayList);
        NugetClient client = context.mock(NugetClient.class);
        ClientFactory clientFactory = context.mock(ClientFactory.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(clientFactory).createClient();
        expectations.will(returnValue(client));
        expectations.atLeast(0).of(client).close();
        addExpectation(expectations, client, 200, 0, createPackageFeed("feed-1", 200, 1));
        addExpectation(expectations, client, 200, 200, createPackageFeed("feed-2", 200, 201));
        addExpectation(expectations, client, 200, 400, createPackageFeed("feed-3", 200, 401));
        addExpectation(expectations, client, 200, 600, createPackageFeed("feed-4", 200, 601));
        addExpectation(expectations, client, 200, 800, createPackageFeed("feed-5", 200, 801));
        addExpectation(expectations, client, 200, 1000, createPackageFeed("feed-6", 200, 1001));
        addExpectation(expectations, client, 200, 1200, createPackageFeed("feed-7", 200, 1201));
        addExpectation(expectations, client, 200, 1400, createPackageFeed("feed-8", 200, 1401));
        addExpectation(expectations, client, 200, 1600, createPackageFeed("feed-9", 200, 1601));
        addExpectation(expectations, client, 200, 1800, createPackageFeed("feed-10", 200, 1801));
        addExpectation(expectations, client, 200, 2000, createPackageFeed("feed-11", 200, 2001));
        addExpectation(expectations, client, 200, 2200, createPackageFeed("feed-12", 200, 2201));
        addExpectation(expectations, client, 200, 2400, createPackageFeed("feed-13", 200, 2401));
        addExpectation(expectations, client, 200, 2600, createPackageFeed("feed-14", 200, 2601));
        addExpectation(expectations, client, 200, 2800, createPackageFeed("feed-15", 200, 2801));
        addExpectation(expectations, client, 200, 3000, createPackageFeed("feed-16", 200, 3001));
        addExpectation(expectations, client, 200, 3200, createPackageFeed("feed-17", 200, 3201));
        addExpectation(expectations, client, 200, 3400, createPackageFeed("feed-18", 200, 3401));
        addExpectation(expectations, client, 200, 3600, createPackageFeed("feed-19", 200, 3601));
        addExpectation(expectations, client, 200, 3800, createPackageFeed("feed-20", 200, 3801));
        context.checking(expectations);
        GetRemotePackageFeedAction instance = new GetRemotePackageFeedAction(200, arrayList, 0, 4000, clientFactory);
        //WHEN
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(instance);
        //THEN
        context.assertIsSatisfied();
        assertThat(arrayList.size(), is(equalTo(4000)));
    }

    /**
     * Проверка загрузки пакетов за один раз
     *
     * @throws NugetFormatException некорректный формат версии пакета
     * @throws IOException ошибка чтения пакета
     * @throws URISyntaxException некорректный синтаксис URI
     */
    @Test
    public void testLoadPackages() throws NugetFormatException, IOException, URISyntaxException {
        //GIVEN
        List<RemoteNupkg> nupkgs = new ArrayList<>();
        NugetClient client = context.mock(NugetClient.class);
        ClientFactory clientFactory = context.mock(ClientFactory.class);
        GetRemotePackageFeedAction action = new GetRemotePackageFeedAction(5, nupkgs, 0, 200, clientFactory);
        Expectations expectations = new Expectations();
        addExpectation(expectations, client, 5, 0, createPackageFeed("feed-1", createPackageEntry("package-1", "1.2.3"), createPackageEntry("package-2", "1.2.3")));
        addExpectation(expectations, client, 2, 2, createPackageFeed("feed-2", createPackageEntry("package-3", "1.2.3"), createPackageEntry("package-4", "1.2.3")));
        addExpectation(expectations, client, 2, 4, createPackageFeed("feed-3"));
        expectations.oneOf(client).close();
        expectations.atLeast(0).of(clientFactory).createClient();
        expectations.will(returnValue(client));
        context.checking(expectations);
        //WHEN
        action.loadPackages();
        //THEN
        assertThat(nupkgs.size(), is(equalTo(4)));
        assertThat(nupkgs.get(0).getId(), is(equalTo("package-1")));
        assertThat(nupkgs.get(1).getId(), is(equalTo("package-2")));
        assertThat(nupkgs.get(2).getId(), is(equalTo("package-3")));
        assertThat(nupkgs.get(3).getId(), is(equalTo("package-4")));
    }

    /**
     * Добавление ожидаемого вызова удаленного хранилища
     *
     * @param expectations ожидаемые вызовы
     * @param client заглушка клиента удаленного хранилища
     * @param top количество пакетов
     * @param skip пропустить пакетов
     * @param packageFeed список пакетов, который вернет метод
     * @throws IOException ошибка чтения из удаленного хранилища
     * @throws URISyntaxException ошибка в синтаксисе URI хранилища
     */
    private void addExpectation(Expectations expectations,
            NugetClient client, int top, int skip, PackageFeed packageFeed) throws IOException, URISyntaxException {
        expectations.atLeast(0).of(client).getPackages(
                expectations.with((String) null),
                expectations.with((String) null),
                expectations.with(top),
                expectations.with((String) null),
                expectations.with(skip));
        expectations.will(returnValue(packageFeed));
    }

    /**
     * @param name имя объекта сообщения
     * @param packageEntrys вложения
     * @return RSS сообщение
     */
    private PackageFeed createPackageFeed(String name, PackageEntry... packageEntrys) {
        final PackageFeed packageFeed = context.mock(PackageFeed.class, name);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(packageFeed).getEntries();
        expectations.will(returnValue(new ArrayList<>(Arrays.asList(packageEntrys))));
        context.checking(expectations);
        return packageFeed;
    }

    /**
     * @param name имя объекта сообщения
     * @param count количество вложений
     * @param skip с какого идентификатора начать нумерацию пакетов
     * @return RSS сообщение
     * @throws NugetFormatException некорректная версия тестового пакета
     */
    private PackageFeed createPackageFeed(String name, int count, int skip) throws NugetFormatException {
        final PackageFeed packageFeed = context.mock(PackageFeed.class, name);
        Expectations expectations = new Expectations();
        ArrayList<PackageEntry> entrys = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entrys.add(createPackageEntry("Package-" + (skip + i), "1.2.3"));
        }
        expectations.atLeast(0).of(packageFeed).getEntries();
        expectations.will(returnValue(new ArrayList<>(entrys)));
        context.checking(expectations);
        return packageFeed;
    }

    /**
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return пакет RSS
     * @throws NugetFormatException некорректная версия пакета
     */
    private PackageEntry createPackageEntry(String id, String version) throws NugetFormatException {
        PackageEntry packageEntry = new PackageEntry();
        packageEntry.setTitle(id);
        packageEntry.getProperties().setVersion(Version.parse(version));
        packageEntry.getProperties().setPackageHash("eoLGkBGTbHl1QsfOcTAx4mmIuTRs8e+wvxhaERmEuqjUSHiTdmiqRrtE1+exxR3Rh5ar0H3QXbGPpR9XsIqK2Q==");
        packageEntry.getProperties().setPackageSize(Long.valueOf(0));
        packageEntry.setContent("http://localhost:8090/nuget/download/" + id + "/" + version);
        return packageEntry;
    }
}
