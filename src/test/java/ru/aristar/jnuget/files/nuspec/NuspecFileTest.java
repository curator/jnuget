package ru.aristar.jnuget.files.nuspec;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.Reference;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Framework;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.VersionRange;
import ru.aristar.jnuget.rss.PackageEntry;

/**
 *
 * @author sviridov
 */
public class NuspecFileTest {

    /**
     * Тест разбора файла спецификации из XML
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseMethod() throws Exception {
        final String fileName = "/nuspec/test.nuspec.xml";
        NuspecFile result = NuspecFile.Parse(NuspecFileTest.class.getResourceAsStream(fileName));
        assertEquals("Идентификатор пакета", "Neolant.ProjectWise.IsolationLevel.Implementation", result.getId());
        assertEquals("Версия пакета", Version.parse("1.4.7.550"), result.getVersion());
        assertEquals("Краткое описание", "Реализация уровня изоляции ProjecWise API", result.getTitle());
        assertEquals("Авторы", "НЕОЛАНТ", result.getAuthors());
        assertEquals("Владельцы", "НЕОЛАНТ", result.getOwners());
        assertEquals("Требуется подтверждение лицензии", false, result.isRequireLicenseAcceptance());
        assertEquals("Описание", "Реализация контрактов уровня изоляции ProjecWise API", result.getDescription());
        assertEquals("Права", "НЕОЛАНТ", result.getCopyright());
    }

    /**
     * Разбор файла спецификации из XML, если указаны ссылки на файлы
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseWithReferences() throws Exception {
        // GIVEN
        final String fileName = "/nuspec/NUnit.nuspec.xml";
        Reference dll = new Reference().setFile("nunit.framework.dll");
        Reference xml = new Reference().setFile("nunit.framework.xml");
        Reference[] references = new Reference[]{dll, xml};
        String[] tags = new String[]{"Unit", "test"};

        // WHEN
        NuspecFile result = NuspecFile.Parse(NuspecFileTest.class.getResourceAsStream(fileName));

        // THEN
        assertEquals("Идентификатор пакета", "NUnit", result.getId());
        assertEquals("Версия пакета", Version.parse("2.5.9.10348"), result.getVersion());
        assertEquals("Авторы", "NUnit", result.getAuthors());
        assertEquals("Владельцы", "NUnit", result.getOwners());
        assertEquals("Требуется подтверждение лицензии", false, result.isRequireLicenseAcceptance());
        assertEquals("Описание", "Пакет модульного тестирования", result.getDescription());
        assertEquals("Права", "Copyright 2011", result.getCopyright());
        assertEquals("Количество меток", tags.length, result.getTags().size());
        assertArrayEquals("Метки", tags, result.getTags().toArray());
        assertEquals("Количество ссылок", references.length, result.getReferences().size());
        assertArrayEquals("Ссылки", references, result.getReferences().toArray());
    }

    /**
     * Разбор файла спецификации из XML, если указаны зависимости пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseWithDependencies() throws Exception {
        // GIVEN
        final String fileName = "/nuspec/NHibernate.nuspec.xml";
        Dependency dep = new Dependency();
        dep.setId("Iesi.Collections");
        dep.versionRange = VersionRange.parse("3.2.0.4000");
        Dependency[] dependencies = new Dependency[]{dep};
        String[] tags = new String[]{"ORM", "DataBase", "DAL", "ObjectRelationalMapping"};

        // WHEN
        NuspecFile result = NuspecFile.Parse(NuspecFileTest.class.getResourceAsStream(fileName));

        // THEN
        assertEquals("Идентификатор пакета", "NHibernate", result.getId());
        assertEquals("Версия пакета", Version.parse("3.2.0.4000"), result.getVersion());
        assertEquals("Авторы", "NHibernate community, Hibernate community", result.getAuthors());
        assertEquals("Владельцы", "NHibernate community, Hibernate community", result.getOwners());
        assertEquals("Требуется подтверждение лицензии", false, result.isRequireLicenseAcceptance());
        assertEquals("Описание",
                "NHibernate is a mature, open source object-relational mapper for the .NET framework. It's actively developed , fully featured and used in thousands of successful projects.",
                result.getDescription());
        assertEquals("Краткое описание",
                "NHibernate is a mature, open source object-relational mapper for the .NET framework. It's actively developed , fully featured and used in thousands of successful projects.",
                result.getSummary());
        assertEquals("Количество меток", tags.length, result.getTags().size());
        assertArrayEquals("Метки", tags, result.getTags().toArray());
        assertEquals("Количество зависимостей", dependencies.length, result.getDependencies().size());
        assertArrayEquals("Зависимости", dependencies, result.getDependencies().toArray());
    }

    /**
     * Проверка получения примечаний к релизу
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseReleaseNotes() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/nuspec/FluentAssertions.nuspec.xml");
        //WHEN
        NuspecFile nuspecFile = NuspecFile.Parse(inputStream);
        //THEN
        assertEquals("Примечания к релизу", "And() extension method to "
                + "TimeSpanConversionExtensions to support 4.Hours()."
                + "And(30.Minutes())", nuspecFile.getReleaseNotes());
    }

    /**
     * Проверка на соответствие старой схеме
     * http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseOldScheme() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/nuspec/NLog.nuspec.xml");
        //WHEN
        NuspecFile nuspecFile = NuspecFile.Parse(inputStream);
        //THEN
        assertEquals("Идентификатор пакета", "NLog", nuspecFile.getId());
    }

    /**
     * Проверка извлечения информации из спецификации, корневой элемент которой
     * не имеет пространства имен
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseWithNoNamespaceRootElement() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/nuspec/PostSharp.nuspec.xml");
        //WHEN
        NuspecFile nuspecFile = NuspecFile.Parse(inputStream);
        //THEN
        assertEquals("Идентификатор пакета", "PostSharp", nuspecFile.getId());
    }

    /**
     * Тест создания спецификации пакета из RSS описания пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateFromPackageEntry() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/rss/entry/Moq.xml");
        PackageEntry entry = PackageEntry.parse(inputStream);
        //WHEN
        NuspecFile nuspecFile = new NuspecFile(entry);
        //THEN
        assertEquals("Идентификатор пакета", "Moq", nuspecFile.getId());
        assertEquals("Версия пакета", Version.parse("4.0.10827"), nuspecFile.getVersion());
        assertEquals("Краткое описание", "", nuspecFile.getSummary());
        assertEquals("Права", "", nuspecFile.getCopyright());
        assertArrayEquals("Метки", new String[]{"Unit", "test", "Mock"}, nuspecFile.getTags().toArray());
        assertEquals("Описание", "Mock для unit тестов", nuspecFile.getDescription());
        assertEquals("Требуется подтверждение лицензии", false, nuspecFile.isRequireLicenseAcceptance());
    }

    /**
     * Тест создания спецификации пакета с фиксированной версией зависимости
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateWithFixedDependencyVersion() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/nuspec/fixed.dependency.nuspec.xml");
        //WHEN
        NuspecFile nuspecFile = NuspecFile.Parse(inputStream);
        List<Dependency> dependencys = nuspecFile.getDependencies();
        //THEN
        assertEquals("Количество зависимостей", dependencys.size(), 1);
    }

    /**
     * Тест создания спецификации пакета с некорректной версией
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test(expected = NugetFormatException.class)
    public void testCreateWithIncorrectVersion() throws Exception {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/nuspec/incorrect.version.nuspec.xml");
        //WHEN
        NuspecFile.Parse(inputStream);
    }

    /**
     * Анализ файла, содржащего зависимости от встроеных ы фреймворк сборок
     *
     * @throws NugetFormatException тестовый XML не соответствует спецификации
     * NuGet
     */
    @Test
    public void testParseWithFrameworkAssemblies() throws NugetFormatException {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/nuspec/Extended.Wpf.Toolkit.nuspec.xml");
        //WHEN
        NuspecFile result = NuspecFile.Parse(inputStream);
        //THEN
        assertThat(result.getFrameworkAssembly().size(), is(equalTo(5)));
        assertThat(result.getFrameworkAssembly().get(0).getAssemblyName(), is(equalTo("PresentationCore")));
        assertThat(result.getFrameworkAssembly().get(0).getTargetFrameworks(), is(equalTo(EnumSet.of(Framework.net35, Framework.net40))));

        assertThat(result.getFrameworkAssembly().get(1).getAssemblyName(), is(equalTo("PresentationFramework")));
        assertThat(result.getFrameworkAssembly().get(1).getTargetFrameworks(), is(equalTo(EnumSet.of(Framework.net35, Framework.net40))));

        assertThat(result.getFrameworkAssembly().get(2).getAssemblyName(), is(equalTo("WindowsBase")));
        assertThat(result.getFrameworkAssembly().get(2).getTargetFrameworks(), is(equalTo(EnumSet.of(Framework.net35, Framework.net40))));

        assertThat(result.getFrameworkAssembly().get(3).getAssemblyName(), is(equalTo("System")));
        assertThat(result.getFrameworkAssembly().get(3).getTargetFrameworks(), is(equalTo(EnumSet.of(Framework.net35, Framework.net40))));

        assertThat(result.getFrameworkAssembly().get(4).getAssemblyName(), is(equalTo("System.Xaml")));
        assertThat(result.getFrameworkAssembly().get(4).getTargetFrameworks(), is(equalTo(EnumSet.of(Framework.net40))));
    }

    /**
     * Анализ XML содержащего груповые зависимости (с версии NuGet 2.0)
     *
     * @throws NugetFormatException тестовый XML не соответствует спецификации
     * NuGet
     */
    @Test
    public void testParseWithGroupDependencies() throws NugetFormatException {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/nuspec/group.dependencies.nuspec.xml");
        //WHEN
        NuspecFile result = NuspecFile.Parse(inputStream);
        //THEN
        assertThat(result, is(not(nullValue())));
        assertThat(result.getDependencies(), is(not(nullValue())));
        assertThat(result.getDependencies().size(), is(equalTo(3)));
        assertThat(result.getDependenciesGroups().size(), is(equalTo(3)));
        assertThat(result.getDependenciesGroups().get(0).getTargetFramework(), is(nullValue()));
        assertThat(result.getDependenciesGroups().get(1).getTargetFramework(), is(equalTo(Framework.net40)));
        assertThat(result.getDependenciesGroups().get(2).getTargetFramework(), is(equalTo(Framework.sl30)));
    }

    /**
     * Проверка на корректность работы с новым пространством имен
     *
     * @throws NugetFormatException тестовый XML не соответствует спецификации
     * NuGet
     */
    @Test
    public void testParseNewNamespace() throws NugetFormatException {
        //GIVEN
        InputStream inputStream = NuspecFileTest.class.getResourceAsStream("/nuspec/Spring.Data.nuspec.xml");
        //WHEN
        NuspecFile result = NuspecFile.Parse(inputStream);
        //THEN
        assertThat(result, is(notNullValue()));
    }
}
