package ru.aristar.jnuget.rss;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Dependency;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.NuspecFile;

/**
 *
 * @author sviridov
 */
public class EntryPropertiesTest {

    /**
     * Проверка преобразования файла спецификации в свойства вложения в RSS
     * сообщении
     *
     * @throws NugetFormatException данные в ресурсе не соответствуют формату
     * NuGet
     */
    @Test
    public void testConvertNuspecToEntryProperties() throws NugetFormatException {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/nuspec/NUnit.nuspec.xml");
        NuspecFile nuspecFile = NuspecFile.Parse(inputStream);
        EntryProperties properties = new EntryProperties();
        //WHEN        
        properties.setNuspec(nuspecFile);
        //THEN
        assertEquals("Версия пакета", new Version(2, 5, 9, "10348"), properties.getVersion());
        assertEquals("Заголовок", "", properties.getTitle());
        assertEquals("URL иконки", "", properties.getIconUrl());
        assertEquals("URL лицензии", "", properties.getLicenseUrl());
        assertEquals("URL проекта", "", properties.getProjectUrl());
        assertEquals("URL отчета", "", properties.getReportAbuseUrl());
        assertEquals("Требуется лицензия", false, properties.getRequireLicenseAcceptance());
        assertEquals("Описание пакета", "Пакет модульного тестирования", properties.getDescription());
        assertEquals("Замечания крелизу", "", properties.getReleaseNotes());
        assertEquals("Язык", "", properties.getLanguage());
        assertEquals("Стоимость пакета", Double.valueOf(0), properties.getPrice());
        assertEquals("Зависимости пакета", "", properties.getDependencies());
        assertEquals("Внешний URI", "", properties.getExternalPackageUri());
        assertEquals("Категории", "", properties.getCategories());
        assertEquals("Права", "Copyright 2011", properties.getCopyright());
        assertEquals("Тип пакета", "", properties.getPackageType());
        assertArrayEquals("Теги пакета", new String[]{"Unit", "test"}, properties.getTags().toArray());
        assertEquals("Общее описание", "", properties.getSummary());
    }

    /**
     * Проверка генерации информации о пакете с зависимостями
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testConvertNuspecWithDependencies() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/nuspec/Dependencies.nuspec.xml");
        NuspecFile nuspecFile = NuspecFile.Parse(inputStream);
        EntryProperties properties = new EntryProperties();
        //WHEN        
        properties.setNuspec(nuspecFile);
        //THEN
        assertEquals("Зависимости пакета", "NLog:2.0.0.2000", properties.getDependencies());
    }

    /**
     * Тест распознавания свойств пакета (RSS) из XML
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testParseProperties() throws Exception {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/rss/entry/properties/NUnit.properties.xml");
        //WHEN
        EntryProperties entryProperties = EntryProperties.parse(inputStream);
        //THEN
        assertEquals("Версия пакета", "2.5.9.10348", entryProperties.getVersion().toString());
        assertEquals("Заголовок", "", entryProperties.getTitle());
        assertEquals("URL иконки", "", entryProperties.getIconUrl());
        assertEquals("URL лицензии", "", entryProperties.getLicenseUrl());
        assertEquals("URL проекта", "", entryProperties.getProjectUrl());
        assertEquals("URL отчета", "", entryProperties.getReportAbuseUrl());
        assertEquals("Количество загрузок пакета", Integer.valueOf(-1), entryProperties.getDownloadCount());
        assertEquals("Количество загрузок версий", Integer.valueOf(-1), entryProperties.getVersionDownloadCount());
        assertEquals("Рейтинг (количество)", Integer.valueOf(0), entryProperties.getRatingsCount());
        assertEquals("Рейтинг версии (количество)", Integer.valueOf(-1), entryProperties.getVersionRatingsCount());
        assertEquals("Рейтинг", Double.valueOf(-1), entryProperties.getRating());
        assertEquals("Рейтинг версии", Double.valueOf(-1), entryProperties.getVersionRating());
        assertEquals("Требуется лицензия", false, entryProperties.getRequireLicenseAcceptance());
        assertEquals("Описание пакета", "Пакет модульного тестирования", entryProperties.getDescription());
        assertEquals("Замечания крелизу", "", entryProperties.getReleaseNotes());
        assertEquals("Язык", "", entryProperties.getLanguage());
        assertEquals("Дата публикации пакета", javax.xml.bind.DatatypeConverter.parseDateTime("2011-09-23T05:18:55.5327281Z").getTime(), entryProperties.getPublished());
        assertEquals("Стоимость пакета", Double.valueOf(0), entryProperties.getPrice());
        assertEquals("Зависимости пакета", "", entryProperties.getDependencies());
        assertEquals("Хеш пакета", "CoknSJBGJ7kao2P6y9E9BuL1IkhP5LLhZ+ImtsgdxzFDpjs0QtRVOV8kxysakJu3cvw5O0hImcnVloCaQ9+Nmg==", entryProperties.getPackageHash());
        assertEquals("Размер пакета", Long.valueOf(214905), entryProperties.getPackageSize());
        assertEquals("Внешний URI", "", entryProperties.getExternalPackageUri());
        assertEquals("Категории", "", entryProperties.getCategories());
        assertEquals("Права", "", entryProperties.getCopyright());
        assertEquals("Тип пакета", "", entryProperties.getPackageType());
        assertArrayEquals("Теги пакета", new String[]{"Unit", "test"}, entryProperties.getTags().toArray());
        assertEquals("Это последняя версия", true, entryProperties.getIsLatestVersion());
        assertEquals("Общее описание", "", entryProperties.getSummary());
    }

    /**
     * Тест получения списка зависимостей, состоящего из одного элемента
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetDependenciesListFromOneElement() throws Exception {
        //GIVEN        
        EntryProperties properties = new EntryProperties();
        properties.setDependencies("A:1.2.3.4");
        //WHEN
        List<Dependency> result = properties.getDependenciesList();
        //THEN
        assertArrayEquals("Список зависимостей",
                new Dependency[]{Dependency.parseString("A:1.2.3.4")},
                result.toArray(new Dependency[0]));

    }

    /**
     * Тест получения списка зависимостей, состоящего из нескольких элементов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetDependenciesList() throws Exception {
        //GIVEN        
        EntryProperties properties = new EntryProperties();
        properties.setDependencies("A:1.2.3.4|B:1.2.3.4");
        //WHEN
        List<Dependency> result = properties.getDependenciesList();
        //THEN
        assertArrayEquals("Список зависимостей",
                new Dependency[]{Dependency.parseString("A:1.2.3.4"),
                    Dependency.parseString("B:1.2.3.4")},
                result.toArray(new Dependency[0]));

    }

    /**
     * Тест получения списка зависимостей, состоящего из нескольких элементов
     * разделенных вертикальной чертой
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetDependenciesListPipeLinrSeparated() throws Exception {
        //GIVEN        
        EntryProperties properties = new EntryProperties();
        properties.setDependencies("adjunct-System.DataStructures.SparsePascalSet:2.2.0|"
                + "adjunct-XUnit.Should.BooleanExtensions:2.0.0|"
                + "adjunct-XUnit.Should.ObjectExtensions:2.0.0|"
                + "xunit:1.8.0.1549");
        //WHEN
        List<Dependency> result = properties.getDependenciesList();
        //THEN
        assertArrayEquals("Список зависимостей",
                new Dependency[]{
                    Dependency.parseString("adjunct-System.DataStructures.SparsePascalSet:2.2.0"),
                    Dependency.parseString("adjunct-XUnit.Should.BooleanExtensions:2.0.0"),
                    Dependency.parseString("adjunct-XUnit.Should.ObjectExtensions:2.0.0"),
                    Dependency.parseString("xunit:1.8.0.1549")
                },
                result.toArray(new Dependency[0]));

    }

    /**
     * Тест получения списка зависимостей, для некорректного списка зависимостей
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test(expected = NugetFormatException.class)
    public void testGetDependenciesListFromEmptyString() throws Exception {
        //GIVEN        
        EntryProperties properties = new EntryProperties();
        properties.setDependencies("eres");
        //WHEN
        properties.getDependenciesList();
    }

    /**
     * Проверка построения строки списка зависимостей
     *
     * @throws NugetFormatException строка зависимости или версия зависимости
     * имеют некорректный формат
     */
    @Test
    public void testSetDependenciesList() throws NugetFormatException {
        //GIVEN
        ArrayList<Dependency> dependencys = new ArrayList<>();
        dependencys.add(Dependency.parseString("package1:1.2.3"));
        dependencys.add(Dependency.parseString("package2:3.2.1"));
        EntryProperties properties = new EntryProperties();
        //WHEN
        properties.setDependenciesList(dependencys);
        //THEN
        assertThat(properties.getDependencies(), is(equalTo("package1:1.2.3, package2:3.2.1")));
    }
}
