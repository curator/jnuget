package ru.aristar.jnuget.ui.descriptors;

import java.net.URL;
import java.util.Collection;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ru.aristar.jnuget.sources.ClassicPackageSource;
import ru.aristar.jnuget.sources.MavenStylePackageSource;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.push.PushStrategy;
import ru.aristar.jnuget.sources.push.SimplePushStrategy;

/**
 * Тесты фабрики дескрипторов классов
 *
 * @author sviridov
 */
public class DescriptorsFactoryTest {

    /**
     * Проверка получения дескриптора источника пакетов
     */
    @Test
    public void testGetPackageSourceDescriptor() {
        //GIVEN
        DescriptorsFactory descriptorsFactory = DescriptorsFactory.getInstance();
        //WHEN
        ObjectDescriptor descriptor = descriptorsFactory.getPackageSourceDescriptor(MavenStylePackageSource.class);
        //THEN
        assertThat(descriptor, is(not(nullValue())));
        assertThat(descriptor, is(instanceOf(MavenStylePackageSourceDescriptor.class)));
    }

    /**
     * Проверка получения дескриптора стратегии фиксации
     */
    @Test
    public void testGetPushStrategyDescriptor() {
        //GIVEN
        DescriptorsFactory descriptorsFactory = DescriptorsFactory.getInstance();
        //WHEN
        ObjectDescriptor<? extends PushStrategy> descriptor = descriptorsFactory.getPushStrategyDescriptor(SimplePushStrategy.class);
        //THEN
        assertThat(descriptor, is(not(nullValue())));
        assertThat(descriptor, is(instanceOf(AbstractObjectDescriptor.class)));
        assertEquals(SimplePushStrategy.class, descriptor.getObjectClass());
        assertThat(descriptor.getProperties().get(0).getDescription(), is(equalTo("Разрешена или нет публикация")));
    }

    /**
     * Тест создания дескриптора для анотированного класса ClassicPackageSource
     */
    @Test
    public void testLoadDescriptors() {
        //GIVEN
        DescriptorsFactory descriptorsFactory = DescriptorsFactory.getInstance();
        URL url = this.getClass().getResource("/ui/testDescriptors.list");
        //WHEN
        Collection<ObjectDescriptor<PackageSource>> result = descriptorsFactory.loadDescriptors(url, PackageSource.class);
        //THEN
        assertThat(result, is(not(nullValue())));
        assertThat(result.size(), is(equalTo(1)));
        ObjectDescriptor<PackageSource> descriptor = result.iterator().next();
        assertThat(descriptor, is(not(nullValue())));
        assertThat(descriptor, is(instanceOf(AbstractObjectDescriptor.class)));
        assertEquals(ClassicPackageSource.class, descriptor.getObjectClass());
        assertThat(descriptor.getProperties().size(), is(equalTo(1)));
        ObjectProperty property = descriptor.getProperties().toArray(new ObjectProperty[0])[0];
        assertThat(property.getDescription(), is(equalTo("Имя каталога, в котором будут храниться пакеты")));
    }
}
