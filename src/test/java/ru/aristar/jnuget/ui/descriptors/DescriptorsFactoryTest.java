package ru.aristar.jnuget.ui.descriptors;

import java.io.InputStream;
import org.junit.AfterClass;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import ru.aristar.jnuget.sources.ClassicPackageSource;

/**
 *
 * @author sviridov
 */
public class DescriptorsFactoryTest {

    @Test
    public void testGetDescriptor() throws Exception {
        //GIVEN
        DescriptorsFactory descriptorsFactory = DescriptorsFactory.getInstance();
        //WHEN
        PackageSourceDescriptor descriptor = descriptorsFactory.getPackageSourceDescriptor(ClassicPackageSource.class);
        //THEN
        assertThat(descriptor, is(not(nullValue())));
        assertThat(descriptor, is(instanceOf(ClassicPackageSourceDescriptor.class)));
    }
}
