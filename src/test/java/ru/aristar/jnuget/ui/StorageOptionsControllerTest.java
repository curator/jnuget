package ru.aristar.jnuget.ui;

import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.sources.push.RemoveOldVersionTrigger;
import ru.aristar.jnuget.ui.StorageOptionsController.Property;

/**
 * Тесты контроллера настроек хранилища
 *
 * @author sviridov
 */
public class StorageOptionsControllerTest {

    /**
     * Проверка получения описания для класса триггера RemoveOldVersionTrigger
     */
    @Test
    public void testGetObjectProperties() {
        //GIVEN
        StorageOptionsController controller = new StorageOptionsController();
        RemoveOldVersionTrigger trigger = new RemoveOldVersionTrigger();
        final int packageCount = 100500;
        trigger.setMaxPackageCount(packageCount);
        //WHEN
        ArrayList<Property> result = controller.getObjectProperties(trigger);
        //THEN
        assertThat(result.size(), is(equalTo(1)));
        assertThat(result.get(0).getName(), is(equalTo("maxPackageCount")));
        assertThat(result.get(0).getValue(), is(equalTo(Integer.valueOf(packageCount).toString())));
    }
}
