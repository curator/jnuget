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
        Expectations expectations = new Expectations();
        addExpectation(expectations, client, 200, 0, createPackageFeed("feed-1", createPackageEntry("package-1", "1.2.3")));
        addExpectation(expectations, client, 200, 200, createPackageFeed("feed-2", createPackageEntry("package-2", "1.2.3")));
        addExpectation(expectations, client, 200, 400, createPackageFeed("feed-3", createPackageEntry("package-3", "1.2.3")));
        addExpectation(expectations, client, 200, 600, createPackageFeed("feed-4", createPackageEntry("package-4", "1.2.3")));
        addExpectation(expectations, client, 200, 800, createPackageFeed("feed-5", createPackageEntry("package-5", "1.2.3")));
        addExpectation(expectations, client, 200, 1000, createPackageFeed("feed-6", createPackageEntry("package-6", "1.2.3")));
        addExpectation(expectations, client, 200, 1200, createPackageFeed("feed-7", createPackageEntry("package-7", "1.2.3")));
        addExpectation(expectations, client, 200, 1400, createPackageFeed("feed-8", createPackageEntry("package-8", "1.2.3")));
        addExpectation(expectations, client, 200, 1600, createPackageFeed("feed-9", createPackageEntry("package-9", "1.2.3")));
        addExpectation(expectations, client, 200, 1800, createPackageFeed("feed-10", createPackageEntry("package-10", "1.2.3")));
        addExpectation(expectations, client, 200, 2000, createPackageFeed("feed-11", createPackageEntry("package-11", "1.2.3")));
        addExpectation(expectations, client, 200, 2200, createPackageFeed("feed-12", createPackageEntry("package-12", "1.2.3")));
        addExpectation(expectations, client, 200, 2400, createPackageFeed("feed-13", createPackageEntry("package-13", "1.2.3")));
        addExpectation(expectations, client, 200, 2600, createPackageFeed("feed-14", createPackageEntry("package-14", "1.2.3")));
        addExpectation(expectations, client, 200, 2800, createPackageFeed("feed-15", createPackageEntry("package-15", "1.2.3")));
        addExpectation(expectations, client, 200, 3000, createPackageFeed("feed-16", createPackageEntry("package-16", "1.2.3")));
        addExpectation(expectations, client, 200, 3200, createPackageFeed("feed-17", createPackageEntry("package-17", "1.2.3")));
        addExpectation(expectations, client, 200, 3400, createPackageFeed("feed-18", createPackageEntry("package-18", "1.2.3")));
        addExpectation(expectations, client, 200, 3600, createPackageFeed("feed-19", createPackageEntry("package-19", "1.2.3")));
        addExpectation(expectations, client, 200, 3800, createPackageFeed("feed-20", createPackageEntry("package-20", "1.2.3")));

        context.checking(expectations);
        GetRemotePackageFeedAction instance = new GetRemotePackageFeedAction(200, arrayList, 0, 4000, client);
        //WHEN
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(instance);
        //THEN
        context.assertIsSatisfied();
        assertThat(arrayList.size(), is(equalTo(20)));
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
