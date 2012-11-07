package ru.aristar.jnuget.files.nio;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static org.hamcrest.CoreMatchers.*;
import org.jmock.Expectations;
import static org.jmock.Expectations.any;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.TempNupkgFile;

/**
 *
 * @author sviridov
 */
public class NupkgFileSystemTest {

    /**
     * Контекст заглушек
     */
    private Mockery context = new Mockery();

    /**
     * Проверка обхода файлового дерева тестового файла
     *
     * @throws IOException ошибка получения тестовых данных
     * @throws NugetFormatException некорректные тестовые данные
     */
    @Test
    public void testWalkFileTree() throws IOException, NugetFormatException {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile tempNupkgFile = new TempNupkgFile(inputStream);
        final NupkgFileSystem fileSystem = new NupkgFileSystem(tempNupkgFile);
        Path path = fileSystem.getRootDirectories().iterator().next();
        final FileVisitor<Path> fileVisitor = context.mock(FileVisitor.class);
        final NupkgPath rootPath = new NupkgPath(fileSystem, "", true);
        final NupkgPath nuspecFile = new NupkgPath(fileSystem, "NUnit.nuspec", false);
        final NupkgPath contentType = new NupkgPath(fileSystem, "[Content_Types].xml", false);
        final NupkgPath relsPath = new NupkgPath(fileSystem, "_rels", true);
        final NupkgPath relsFile = new NupkgPath(fileSystem, "_rels/.rels", false);
        final NupkgPath packagePath = new NupkgPath(fileSystem, "package", true);
        final NupkgPath libPath = new NupkgPath(fileSystem, "lib", true);
        final NupkgPath lib20Path = new NupkgPath(fileSystem, "lib/net20", true);
        final NupkgPath frameworklib20dllPath = new NupkgPath(fileSystem, "lib/net20/nunit.framework.dll", true);
        final NupkgPath frameworklib20xmlPath = new NupkgPath(fileSystem, "lib/net20/nunit.framework.xml", true);
        final NupkgPath lib11Path = new NupkgPath(fileSystem, "lib/net11", true);
        final NupkgPath framework11dllPath = new NupkgPath(fileSystem, "lib/net11/nunit.framework.dll", false);
        final NupkgPath framework11xmlPath = new NupkgPath(fileSystem, "lib/net11/nunit.framework.xml", false);
        final NupkgPath servicesPath = new NupkgPath(fileSystem, "package/services", true);
        final NupkgPath metadataPath = new NupkgPath(fileSystem, "package/services/metadata", true);
        final NupkgPath propertiesPath = new NupkgPath(fileSystem, "package/services/metadata/core-properties", true);
        final NupkgPath propertiesFile = new NupkgPath(fileSystem, "package/services/metadata/core-properties/c2068561aecd49fb9838b8b0797a65ae.psmdcp", false);

        Expectations expectations = new Expectations() {
            {
                oneOf(fileVisitor).preVisitDirectory(with(rootPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(rootPath, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).preVisitDirectory(with(relsPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(relsPath, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).preVisitDirectory(with(packagePath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(packagePath, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).preVisitDirectory(with(libPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(libPath, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).preVisitDirectory(with(lib20Path), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(lib20Path, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).preVisitDirectory(with(lib11Path), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(lib11Path, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).preVisitDirectory(with(servicesPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(servicesPath, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).preVisitDirectory(with(metadataPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(metadataPath, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).preVisitDirectory(with(propertiesPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
                oneOf(fileVisitor).postVisitDirectory(propertiesPath, null);
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).visitFile(with(framework11dllPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).visitFile(with(propertiesFile), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).visitFile(with(nuspecFile), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).visitFile(with(contentType), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).visitFile(with(frameworklib20dllPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).visitFile(with(relsFile), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).visitFile(with(frameworklib20xmlPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));

                oneOf(fileVisitor).visitFile(with(framework11xmlPath), with(any(BasicFileAttributes.class)));
                will(returnValue(FileVisitResult.CONTINUE));
            }
        };
        context.checking(expectations);
        //WHEN        
        Files.walkFileTree(path, fileVisitor);
        //THEN
        context.assertIsSatisfied();
    }

    /**
     * Проверка получения корневого каталога
     *
     * @throws IOException ошибка получения тестовых данных
     * @throws NugetFormatException некорректные тестовые данные
     */
    @Test
    public void testGetRootDirectories() throws IOException, NugetFormatException {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile tempNupkgFile = new TempNupkgFile(inputStream);
        NupkgFileSystem fileSystem = new NupkgFileSystem(tempNupkgFile);
        final Path rootPath = new NupkgPath(fileSystem, "", true);
        //WHEN
        List<Path> result = Lists.newArrayList(fileSystem.getRootDirectories());
        //THEN
        assertThat(result.size(), is(equalTo(1)));
        assertThat(result.get(0), is(equalTo(rootPath)));
        assertThat(fileSystem.provider().readAttributes(result.get(0), BasicFileAttributes.class).isDirectory(), is(equalTo(true)));

    }

    /**
     * Проверка получения детей
     *
     * @throws IOException ошибка получения тестовых данных
     * @throws NugetFormatException некорректные тестовые данные
     */
    @Test
    public void testGetChild() throws IOException, NugetFormatException {
        //GIVEN
        InputStream inputStream = this.getClass().getResourceAsStream("/NUnit.2.5.9.10348.nupkg");
        TempNupkgFile tempNupkgFile = new TempNupkgFile(inputStream);
        NupkgFileSystem fileSystem = new NupkgFileSystem(tempNupkgFile);
        NupkgPath path = new NupkgPath(fileSystem, "package", true);
        //WHEN
        Set<Path> result = fileSystem.getChildren(path);
        //THEN
        assertThat(result.size(), is(equalTo(1)));
    }

    /**
     * Проверка вхождения путей друг в друга
     */
    @Test
    public void testSubPath() {
        //GIVEN
        NupkgPath up = new NupkgPath(null, "lib/net11", true);
        NupkgPath sub = new NupkgPath(null, "package", true);
        //WHEN
        boolean result = up.startsWith(sub);
        //THEN 
        assertThat(result, is(equalTo(false)));
    }

    /**
     * Проверка получения итератора для пути
     */
    @Test
    public void testIterator() {
        //GIVEN
        NupkgPath path = new NupkgPath(null, "lib/net11", true);
        final Path rootPath = new NupkgPath(null, "", true);
        final Path firstLevelPath = new NupkgPath(null, "lib", true);
        final Path secondLevelPath = new NupkgPath(null, "lib/net11", true);

        //WHEN
        Iterator<Path> iterator = path.iterator();
        ArrayList<Path> result = Lists.newArrayList(iterator);

        //THEN 
        assertThat(result.size(), is(equalTo(3)));
        assertThat(result.get(0), is(equalTo(rootPath)));
        assertThat(result.get(1), is(equalTo(firstLevelPath)));
        assertThat(result.get(2), is(equalTo(secondLevelPath)));
    }
}
