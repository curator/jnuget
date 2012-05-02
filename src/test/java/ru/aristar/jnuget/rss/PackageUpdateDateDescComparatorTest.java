package ru.aristar.jnuget.rss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author sviridov
 */
public class PackageUpdateDateDescComparatorTest {

    /**
     * Контекст заглушек
     */
    private Mockery context = new Mockery() {

        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * @param dateTimeString строка даты/времени
     * @return значение даты/времени
     * @throws ParseException строка имеет некорректный формат
     */
    private Date createDate(String dateTimeString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return simpleDateFormat.parse(dateTimeString);
    }

    /**
     * Проверка сравнения двух пакетов с различными датами обновления
     *
     * @throws ParseException строка даты/времени, использующаяся в тестах имеет
     * некорректный формат
     */
    @Test
    public void testCompare() throws ParseException {
        //GIVEN
        Expectations expectations = new Expectations();
        PackageEntry firstEntry = context.mock(PackageEntry.class, "firstEntry");
        expectations.atLeast(0).of(firstEntry).getUpdated();
        expectations.will(returnValue(createDate("2012.04.27 15:30:00")));
        PackageEntry secondEntry = context.mock(PackageEntry.class, "secondEntry");
        expectations.atLeast(0).of(secondEntry).getUpdated();
        expectations.will(returnValue(createDate("2012.04.27 15:40:00")));
        context.checking(expectations);
        PackageUpdateDateDescComparator instance = new PackageUpdateDateDescComparator();
        //WHEN
        int result = instance.compare(firstEntry, secondEntry);
        //THEN
        assertThat("Результат сравнения пактов", result, is(equalTo(1)));
    }

    /**
     * Проверка сортировки массива пакетов по дате создания
     *
     * @throws ParseException строка даты/времени, использующаяся в тестах имеет
     * некорректный формат
     */
    @Test
    public void testArraySort() throws ParseException {
        //GIVEN
        Expectations expectations = new Expectations();
        PackageEntry firstEntry = context.mock(PackageEntry.class, "firstEntry");
        expectations.atLeast(0).of(firstEntry).getUpdated();
        expectations.will(returnValue(createDate("2012.04.27 15:30:00")));
        PackageEntry secondEntry = context.mock(PackageEntry.class, "secondEntry");
        expectations.atLeast(0).of(secondEntry).getUpdated();
        expectations.will(returnValue(createDate("2012.04.27 15:40:00")));
        context.checking(expectations);
        PackageEntry[] entrys = new PackageEntry[]{firstEntry, secondEntry};
        PackageUpdateDateDescComparator instance = new PackageUpdateDateDescComparator();
        //WHEN
        Arrays.sort(entrys, instance);
        //THEN
        assertArrayEquals("Результат сортировки пакетов", new PackageEntry[]{secondEntry, firstEntry}, entrys);
    }
}
