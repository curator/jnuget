package ru.aristar.jnuget.sources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.ProxyNupkg;
import ru.aristar.jnuget.files.RemoteNupkg;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 * Тесты для индекса пакетов
 *
 * @author sviridov
 */
public class IndexTest {

    /**
     * Контекст для создания заглушек
     */
    private Mockery context = new Mockery();
    /**
     * Уникальный идентификатор заглушки
     */
    private int mockId = 0;

    /**
     * Создает идентификатор фала пакета
     *
     * @param id идентификатор пакета
     * @param version версия пакета
     * @return идентификатор фала пакета
     * @throws Exception некорректный формат версии
     */
    private Nupkg createNupkg(final String id, final String version) throws Exception {
        final Nupkg pack = context.mock(Nupkg.class, "nupkg" + (mockId++));
        context.checking(new Expectations() {
            
            {
                atLeast(0).of(pack).getId();
                will(returnValue(id));
                atLeast(0).of(pack).getVersion();
                will(returnValue(Version.parse(version)));
            }
        });
        
        return pack;
    }

    /**
     * Сортирует массив пакетов сначало по возрастанию идентификатора пакета,
     * затем по возрастанию его версии
     *
     * @param result сортированный массив
     */
    private void sortNupkgArray(Nupkg[] result) {
        Arrays.sort(result, new Comparator<Nupkg>() {
            
            @Override
            public int compare(Nupkg o1, Nupkg o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
    }

    /**
     * Преобразует итератор в массив элементов
     *
     * @param iterator итератор
     * @return массив элементов
     */
    private Nupkg[] iteratorToArray(Iterator<Nupkg> iterator) {
        ArrayList<Nupkg> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result.toArray(new Nupkg[0]);
    }

    /**
     * Проверка получения списка всех пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetAllPackages() throws Exception {
        //GIVEN
        Nupkg[] nupkgs = new Nupkg[]{
            createNupkg("A", "1.1.0"),
            createNupkg("A", "1.2.0"),
            createNupkg("B", "1.1.0"),
            createNupkg("C", "2.1.0"),
            createNupkg("C", "5.1.0")
        };
        
        Index index = new Index();
        index.putAll(nupkgs);

        //WHEN
        Nupkg[] result = iteratorToArray(index.getAllPackages());
        //THEN
        sortNupkgArray(result);
        assertArrayEquals("Полный список пакетов в индексе", nupkgs, result);
    }

    /**
     * Проверка получения размера индекса
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetIndexSize() throws Exception {
        //GIVEN
        Nupkg[] nupkgs = new Nupkg[]{
            createNupkg("A", "1.1.0"),
            createNupkg("A", "1.2.0"),
            createNupkg("A", "1.2.0"),
            createNupkg("B", "1.1.0"),
            createNupkg("C", "2.1.0"),
            createNupkg("C", "5.1.0")
        };
        
        Index index = new Index();
        //WHEN
        index.putAll(nupkgs);
        //THEN
        assertEquals("Размер индекса", 5, index.size());
    }

    /**
     * Проверка получения последних версий пакетов
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetLastVersions() throws Exception {
        //GIVEN
        Nupkg lastA, lastB, lastC;
        Nupkg[] nupkgs = new Nupkg[]{
            createNupkg("A", "1.1.0"),
            lastA = createNupkg("A", "1.2.0"),
            lastB = createNupkg("B", "1.1.0"),
            createNupkg("C", "2.1.0"),
            lastC = createNupkg("C", "5.1.0")
        };
        
        Index index = new Index();
        index.putAll(nupkgs);

        //WHEN
        Nupkg[] result = iteratorToArray(index.getLastVersions());
        //THEN
        sortNupkgArray(result);
        assertArrayEquals("Последние версии пакетов", new Nupkg[]{lastA, lastB, lastC}, result);
    }

    /**
     * Проверка получения всех версий пакета
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetAllPackageVersionById() throws Exception {
        //GIVEN
        Nupkg firstA, lastA;
        Nupkg[] nupkgs = new Nupkg[]{
            firstA = createNupkg("A", "1.1.0"),
            lastA = createNupkg("A", "1.2.0"),
            createNupkg("B", "1.1.0"),
            createNupkg("C", "2.1.0"),
            createNupkg("C", "5.1.0")
        };
        
        Index index = new Index();
        index.putAll(nupkgs);

        //WHEN
        Nupkg[] result = index.getPackageById("A").toArray(new Nupkg[0]);
        //THEN
        sortNupkgArray(result);
        assertArrayEquals("Версии пакета A", new Nupkg[]{firstA, lastA}, result);
    }

    /**
     * Проверка получения всех версий пакета, усли пакета нет в репозитории
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testGetAllPackageVersionByIdWhenNoPackages() throws Exception {
        //GIVEN
        Nupkg[] nupkgs = new Nupkg[]{
            createNupkg("A", "1.1.0"),
            createNupkg("A", "1.2.0"),
            createNupkg("B", "1.1.0"),
            createNupkg("C", "2.1.0"),
            createNupkg("C", "5.1.0")
        };
        
        Index index = new Index();
        index.putAll(nupkgs);
        //WHEN
        Collection<Nupkg> result = index.getPackageById("E");
        //THEN
        assertNotNull(result);
        assertEquals("Версий пакета E нет", 0, result.size());
    }

    /**
     * Проверка возможности сериализации пустого индекса
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testSaveEmptyIndex() throws Exception {
        //GIVEN
        File file = File.createTempFile("index", "index");
        file.delete();
        assertFalse(file.exists());
        
        Index index = new Index();
        //WHEN
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            index.saveTo(fileOutputStream);
        }
        //THEN
        assertTrue("Файл индекса создан", file.exists());
        assertTrue("Размер файла не равен 0", file.getTotalSpace() > 0);
    }

    /**
     * Проверка возможности сериализации непустого индекса
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testSaveNonEmptyIndex() throws Exception {
        //GIVEN
        File file = File.createTempFile("index", "index");
        file.delete();
        assertFalse(file.exists());
        
        Index index = new Index();
        TempNupkgFile tempNupkgFile = new TempNupkgFile(this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg"));
        RemoteNupkg remoteNupkg = new RemoteNupkg(tempNupkgFile.getNuspecFile(), tempNupkgFile.getHash(), mockId, tempNupkgFile.getUpdated(), new URI("http://site.org"));
        ProxyNupkg proxyNupkg = new ProxyNupkg(null, remoteNupkg);
        index.put(proxyNupkg);

        //WHEN
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            index.saveTo(fileOutputStream);
        }
        //THEN
        assertTrue("Файл индекса создан", file.exists());
        assertTrue("Размер файла не равен 0", file.getTotalSpace() > 0);
    }

    /**
     * Проверка возможности чтения пустого индекса из файла
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testLoadEmptyIndex() throws Exception {
        //GIVEN
        File file = File.createTempFile("index", "index");
        file.delete();
        assertFalse(file.exists());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            Index index = new Index();
            index.saveTo(fileOutputStream);
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            //WHEN
            Index index = Index.loadFrom(fileInputStream);
            //THEN
            assertNotNull("Индекс прочитан из файла", index);
            assertFalse("Индекс не содержит пакетов", index.getAllPackages().hasNext());
        }
    }
}
