package ru.aristar.jnuget.sources.push;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Description;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.PackageSource;

/**
 *
 * @author sviridov
 */
public class RemoveOldVersionTriggerTest {

    /**
     * Контекст заглушек
     */
    private Mockery context = new Mockery();

    /**
     * Создает набор пакетов с указанным идентификатором и версиями
     *
     * @param packageId идентификатор пакета
     * @param versions версии пакета
     * @return список заглушек пакета
     * @throws NugetFormatException некорректный формат версии
     */
    private List<Nupkg> createNupkgList(String packageId, String... versions) throws NugetFormatException {
        ArrayList<Nupkg> packages = new ArrayList<>();
        Expectations expectations = new Expectations();
        for (String versionString : versions) {
            Version version = Version.parse(versionString);
            final String mockName = packageId + ":" + version;
            final Nupkg nupkg = context.mock(Nupkg.class, mockName);
            packages.add(nupkg);
            expectations.atLeast(0).of(nupkg).getId();
            expectations.will(returnValue(packageId));
            expectations.atLeast(0).of(nupkg).getVersion();
            expectations.will(returnValue(version));
        }
        context.checking(expectations);
        return packages;
    }

    /**
     * Заглушка, добавляющая версии удаленных пакетов в список
     */
    public class RemovedPackageVersionStub implements Action {

        /**
         * Список для добавления удаленных версий
         */
        public final List<Version> versions;
        /**
         * Список для добавления удаленных идентификаторов
         */
        public final List<String> packageIds;

        /**
         * @param versions список для добавления удаленных версий
         * @param packageIds список для добавления удаленных идентификаторов
         */
        public RemovedPackageVersionStub(List<Version> versions, List<String> packageIds) {
            this.versions = versions;
            this.packageIds = packageIds;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Собирает список вызовов метода в коллекцию");
        }

        @Override
        public Object invoke(Invocation invocation) throws Throwable {
            Object firstArgument = invocation.getParameter(0);
            Object secondArgument = invocation.getParameter(1);
            String id = (String) firstArgument;
            Version version = (Version) secondArgument;
            versions.add(version);
            packageIds.add(id);
            return null;
        }
    }

    /**
     * Проверка срабатывания триггера на пустом хранилище
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveFromEmptyStorage() throws Exception {
        //GIVEN
        Nupkg nupkg = createNupkgList("Nupkg", "0.0.1").get(0);
        final PackageSource packageSource = context.mock(PackageSource.class);
        List<Version> removedVersions = new ArrayList<>();
        List<String> removedIds = new ArrayList<>();
        final RemovedPackageVersionStub addRemovedVersionToList = new RemovedPackageVersionStub(removedVersions, removedIds);
        context.checking(new Expectations() {

            {
                atLeast(0).of(packageSource).getPackages("Nupkg");
                will(returnValue(new ArrayList<Nupkg>()));
                atLeast(0).of(packageSource).removePackage(with(any(String.class)), with(any(Version.class)));
                will(addRemovedVersionToList);
            }
        });
        RemoveOldVersionTrigger trigger = new RemoveOldVersionTrigger();
        trigger.setMaxPackageCount(10);

        //WHEN
        trigger.doAction(nupkg, packageSource);
        //THEN
        assertThat("Удаленные идентификаторы пакета пакета",
                removedIds.toArray(new String[0]),
                equalTo(new String[]{}));
        assertThat("Удаленные версии пакета",
                removedVersions.toArray(new Version[0]),
                equalTo(new Version[]{}));

    }

    /**
     * Проверка срабатывания триггера, если максимально допустимое число пакетов
     * превышено на 1. Должна быть удалена самая младшая версия
     *
     *
     * @throws Exception ошибка впроцессе теста
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveOnePackageWhenMaxCountExceeded() throws Exception {
        //GIVEN
        final List<Nupkg> nupkgs = createNupkgList("Nupkg",
                "0.0.1",
                "0.0.2",
                "0.0.3",
                "0.0.4",
                "0.0.5",
                "0.0.6",
                "0.0.7",
                "0.0.8",
                "0.0.9",
                "0.0.10",
                "0.0.11");
        Nupkg nupkg = nupkgs.get(0);
        List<Version> removedVersions = new ArrayList<>();
        List<String> removedIds = new ArrayList<>();
        final RemovedPackageVersionStub addRemovedVersionToList = new RemovedPackageVersionStub(removedVersions, removedIds);

        final PackageSource packageSource = context.mock(PackageSource.class);
        context.checking(new Expectations() {

            {
                atLeast(0).of(packageSource).getPackages("Nupkg");
                will(returnValue(nupkgs));
                atLeast(0).of(packageSource).removePackage(with(any(String.class)), with(any(Version.class)));
                will(addRemovedVersionToList);
            }
        });
        RemoveOldVersionTrigger trigger = new RemoveOldVersionTrigger();
        trigger.setMaxPackageCount(10);
        //WHEN
        trigger.doAction(nupkg, packageSource);
        //THEN    
        assertThat("Удаленные идентификаторы пакета пакета",
                removedIds.toArray(new String[0]),
                equalTo(new String[]{"Nupkg"}));
        assertThat("Удаленные версии пакета",
                removedVersions.toArray(new Version[0]),
                equalTo(new Version[]{Version.parse("0.0.1")}));
    }
}
