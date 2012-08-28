package ru.aristar.jnuget.ui;

import java.util.List;
import javax.faces.model.DataModel;
import static org.hamcrest.CoreMatchers.*;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.*;
import org.junit.Test;
import ru.aristar.jnuget.files.Nupkg;

/**
 *
 * @author sviridov
 */
public class StorageContentsControllerTest {

    /**
     * Контекст заглушек
     */
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Список переходов, если все пакеты помещаются на одной странице
     */
    @Test
    public void testGetSkipListAllPackagesOnOnePage() {
        //GIVEN
        StorageContentsController controller = new StorageContentsController();
        @SuppressWarnings("unchecked")
        DataModel<Nupkg> dataModel = (DataModel<Nupkg>) context.mock(DataModel.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(dataModel).getRowCount();
        expectations.will(returnValue(100));
        context.checking(expectations);
        controller.packages = dataModel;
        controller.setDisplayCount(200);
        controller.setLow(0);
        //WHEN
        List<Integer> result = controller.getSkipList();
        //THEN
        assertThat(result, is(not(nullValue())));
        assertArrayEquals(new Integer[]{0, 0, 0, 0, 0}, result.toArray(new Integer[0]));
    }

    /**
     * Список переходов, если все пакеты помещаются на двух страницах и текущая
     * первая
     */
    @Test
    public void testGetSkipListAllPackagesOnTwoPage() {
        //GIVEN
        StorageContentsController controller = new StorageContentsController();
        @SuppressWarnings("unchecked")
        DataModel<Nupkg> dataModel = (DataModel<Nupkg>) context.mock(DataModel.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(dataModel).getRowCount();
        expectations.will(returnValue(200));
        context.checking(expectations);
        controller.packages = dataModel;
        controller.setDisplayCount(100);
        controller.setLow(0);
        //WHEN
        List<Integer> result = controller.getSkipList();
        //THEN
        assertThat(result, is(not(nullValue())));
        assertArrayEquals(new Integer[]{0, 0, 0, 100, 100, 100}, result.toArray(new Integer[0]));
    }

    /**
     * Список переходов, если все пакеты помещаются на двух страницах и текущая
     * вторая
     */
    @Test
    public void testGetSkipListAllPackagesOnTwoPageLastPage() {
        //GIVEN
        StorageContentsController controller = new StorageContentsController();
        @SuppressWarnings("unchecked")
        DataModel<Nupkg> dataModel = (DataModel<Nupkg>) context.mock(DataModel.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(dataModel).getRowCount();
        expectations.will(returnValue(200));
        context.checking(expectations);
        controller.packages = dataModel;
        controller.setDisplayCount(100);
        controller.setLow(100);
        //WHEN
        List<Integer> result = controller.getSkipList();
        //THEN
        assertThat(result, is(not(nullValue())));
        assertArrayEquals(new Integer[]{0, 0, 0, 100, 100, 100}, result.toArray(new Integer[0]));
    }

    /**
     * Список переходов, если все пакеты не помещаются на 11 страницах и текущая
     * первая
     */
    @Test
    public void testGetSkipListAllPackagesOnMultiplePageFirstPage() {
        //GIVEN
        StorageContentsController controller = new StorageContentsController();
        @SuppressWarnings("unchecked")
        DataModel<Nupkg> dataModel = (DataModel<Nupkg>) context.mock(DataModel.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(dataModel).getRowCount();
        expectations.will(returnValue(1000));
        context.checking(expectations);
        controller.packages = dataModel;
        controller.setDisplayCount(100);
        controller.setLow(100);
        //WHEN
        List<Integer> result = controller.getSkipList();
        //THEN
        assertThat(result, is(not(nullValue())));
        assertArrayEquals(new Integer[]{0, 0, 0, 100, 200, 300, 400, 500, 600, 200, 900}, result.toArray(new Integer[0]));
    }

    /**
     * Список переходов, если все пакеты не помещаются на 11 страницах и текущая
     * последняя
     */
    @Test
    public void testGetSkipListAllPackagesOnMultiplePageLastPage() {
        //GIVEN
        StorageContentsController controller = new StorageContentsController();
        @SuppressWarnings("unchecked")
        DataModel<Nupkg> dataModel = (DataModel<Nupkg>) context.mock(DataModel.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(dataModel).getRowCount();
        expectations.will(returnValue(1000));
        context.checking(expectations);
        controller.packages = dataModel;
        controller.setDisplayCount(100);
        controller.setLow(900);
        //WHEN
        List<Integer> result = controller.getSkipList();
        //THEN
        assertThat(result, is(not(nullValue())));
        assertArrayEquals(new Integer[]{0, 800, 300, 400, 500, 600, 700, 800, 900, 900, 900}, result.toArray(new Integer[0]));
    }

    /**
     * Список переходов если число пакетов не кратно размеру страницы
     */
    @Test
    public void testGetSkipListRealPackages() {
        //GIVEN
        StorageContentsController controller = new StorageContentsController();
        @SuppressWarnings("unchecked")
        DataModel<Nupkg> dataModel = (DataModel<Nupkg>) context.mock(DataModel.class);
        Expectations expectations = new Expectations();
        expectations.atLeast(0).of(dataModel).getRowCount();
        expectations.will(returnValue(370));
        context.checking(expectations);
        controller.packages = dataModel;
        controller.setDisplayCount(200);
        controller.setLow(0);
        //WHEN
        List<Integer> result = controller.getSkipList();
        //THEN
        assertThat(result, is(not(nullValue())));
        assertArrayEquals(new Integer[]{0, 0, 0, 200, 200, 200}, result.toArray(new Integer[0]));
    }
}
