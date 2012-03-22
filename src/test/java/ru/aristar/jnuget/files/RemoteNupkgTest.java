package ru.aristar.jnuget.files;

import java.io.InputStream;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.rss.PackageFeed;

/**
 * Тесты пакета в удаленном репозитории
 *
 * @author sviridov
 */
public class RemoteNupkgTest {

    /**
     * Проверка создания пакета для удаленного репозитория
     *
     * @throws Exception ошибка в процессе теста
     */
    @Test
    public void testCreateRemoteNupkg() throws Exception {
        //GIVEN
        try (InputStream inputStream = this.getClass().getResourceAsStream("/rss/AutoDiff.xml")) {
            PackageFeed packageFeed = PackageFeed.parse(inputStream);
            //WHEN
            RemoteNupkg remoteNupkg = new RemoteNupkg(packageFeed.getEntries().get(0));
            remoteNupkg.load();
            //THEN
            assertThat("Идентификатор пакета", remoteNupkg.getId(), is("AutoDiff"));
            assertThat("Версия пакета", remoteNupkg.getVersion(), is(Version.parse("0.5.4321.2401")));
        }
    }
}
