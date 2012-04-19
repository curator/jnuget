package ru.aristar.jnuget.sources;

import com.sun.jersey.api.client.UniformInterfaceException;
import java.util.ArrayList;
import java.util.Arrays;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.jmock.Expectations;
import static org.jmock.Expectations.*;
import org.jmock.Mockery;
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
 * Тест удаленного хранилища данных
 *
 * @author sviridov
 */
public class RemotePackageSourceTest {

    /**
     * Контекст для создания заглушек
     */
    private Mockery context = new Mockery() {

        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Проверка получения пакета из удаленного хранилища, если пакетов с таким
     * идентификатором не существует
     *
     * @throws UniformInterfaceException ошибка при создании тестового метода
     * (данные некорректного типа)
     * @throws NugetFormatException версия пакета, использующаяся в тесте имеет
     * некорректный формат
     */
    @Test
    public void testGetLastVersionPackageWhenPackageNotExists() throws UniformInterfaceException, NugetFormatException {
        //GIVEN
        RemotePackageSource packageSource = new RemotePackageSource();
        final NugetClient nugetClient = context.mock(NugetClient.class);
        final PackageFeed packageFeed = createPackageFeed("EmptyFeed");
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(nugetClient).getPackages(
                expectations.with(any(String.class)),
                expectations.with(any(String.class)),
                expectations.with(any(Integer.class)),
                expectations.with(any(String.class)),
                expectations.with(any(Integer.class)));
        expectations.will(returnValue(packageFeed));
        context.checking(expectations);
        packageSource.remoteStorage = nugetClient;
        //WHEN
        RemoteNupkg nupkg = packageSource.getLastVersionPackage("id");
        //THEN
        assertThat("Если пакета не существует должен возвращаться null", nupkg, is(nullValue()));
    }

    /**
     * Проверка получения пакета из удаленного хранилища, cуществует один пакет
     * с указанным идентификатором
     *
     * @throws UniformInterfaceException ошибка при создании тестового метода
     * (данные некорректного типа)
     * @throws NugetFormatException версия пакета, использующаяся в тесте имеет
     * некорректный формат
     */
    @Test
    public void testGetLastVersionPackageWhenExistOnePackage() throws
            UniformInterfaceException,
            NugetFormatException {
        //GIVEN
        RemotePackageSource packageSource = new RemotePackageSource();
        final NugetClient nugetClient = context.mock(NugetClient.class);
        final PackageFeed packageFeed = createPackageFeed("FirstFeed", createPackageEntry("id", "1.2.3"));
        final PackageFeed emptyFeed = createPackageFeed("SecondFeed");
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(nugetClient).getPackages(
                expectations.with(any(String.class)),
                expectations.with(any(String.class)),
                expectations.with(any(Integer.class)),
                expectations.with(any(String.class)),
                expectations.with(0));
        expectations.will(returnValue(packageFeed));
        expectations.atLeast(0).of(nugetClient).getPackages(
                expectations.with(any(String.class)),
                expectations.with(any(String.class)),
                expectations.with(any(Integer.class)),
                expectations.with(any(String.class)),
                expectations.with(200));
        expectations.will(returnValue(emptyFeed));

        expectations.atLeast(0).of(emptyFeed).getEntries();
        expectations.will(returnValue(new ArrayList<>()));

        context.checking(expectations);
        packageSource.remoteStorage = nugetClient;
        //WHEN
        RemoteNupkg nupkg = packageSource.getLastVersionPackage("id");
        //THEN
        assertThat("Пакет должен быть возвращен из метода", nupkg, is(notNullValue()));
        assertThat("Версия пакета", nupkg.getVersion(), is(equal(Version.parse("1.2.3"))));
        assertThat("Идентификатор пакета", nupkg.getId(), is(equal("id")));
    }

    /**
     * Проверка получения пакета из удаленного хранилища, cуществует несколько
     * пакетов с указанным идентификатором
     *
     * @throws UniformInterfaceException ошибка при создании тестового метода
     * (данные некорректного типа)
     * @throws NugetFormatException версия пакета, использующаяся в тесте имеет
     * некорректный формат
     */
    @Test
    public void testGetLastVersionPackageWhenExistMultiplePackage() throws
            UniformInterfaceException,
            NugetFormatException {
        //GIVEN
        RemotePackageSource packageSource = new RemotePackageSource();
        final NugetClient nugetClient = context.mock(NugetClient.class);
        final PackageFeed packageFeed = createPackageFeed("FirstFeed", createPackageEntry("id", "1.2.3"), createPackageEntry("id", "1.2.0"));
        final PackageFeed emptyFeed = createPackageFeed("SecondFeed");
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(nugetClient).getPackages(
                expectations.with(any(String.class)),
                expectations.with(any(String.class)),
                expectations.with(any(Integer.class)),
                expectations.with(any(String.class)),
                expectations.with(0));
        expectations.will(returnValue(packageFeed));
        expectations.atLeast(0).of(nugetClient).getPackages(
                expectations.with(any(String.class)),
                expectations.with(any(String.class)),
                expectations.with(any(Integer.class)),
                expectations.with(any(String.class)),
                expectations.with(200));
        expectations.will(returnValue(emptyFeed));

        expectations.atLeast(0).of(emptyFeed).getEntries();
        expectations.will(returnValue(new ArrayList<>()));

        context.checking(expectations);
        packageSource.remoteStorage = nugetClient;
        //WHEN
        RemoteNupkg nupkg = packageSource.getLastVersionPackage("id");
        //THEN
        assertThat("Пакет должен быть возвращен из метода", nupkg, is(notNullValue()));
        assertThat("Версия пакета", nupkg.getVersion(), is(equal(Version.parse("1.2.3"))));
        assertThat("Идентификатор пакета", nupkg.getId(), is(equal("id")));
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
