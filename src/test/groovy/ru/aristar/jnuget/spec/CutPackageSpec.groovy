package ru.aristar.jnuget.spec

import spock.lang.Specification
import ru.aristar.jnuget.rss.NuPkgToRssTransformer
import ru.aristar.jnuget.NugetContext

/**
 * Спецификации обрезки количества пакетов
 * @author sviridov
 */
class CutPackageSpec extends Specification{

    /**
    * Создание тестового списка объектов
    * @param size желаемый размер списка
    * @return список объектов
    */
    List<Object> testSource(int size){
        size == 0 ? [] : 1..size
    }

    /**
    * Список объектов должен корректно обрезаться
    */
    def "Object list must correct cutting"(){
        setup:
        NuPkgToRssTransformer transformer = new StubNuPkgToRssTransformer();
        expect:
        transformer.cutPackageList(skip, top, testSource(sourceSize)).size() == resultSize
        where:
        sourceSize  | skip  |  top  | resultSize
            0       |   0   |   0   |   0
            30      |   30  |   0   |   0
            10      |   0   |   -1  |   10
            60      |   30  |   -1  |   30
            0       |  100  |  200  |   0
    }
}

/**
* Заглушка для тестирования класса NuPkgToRssTransformer
*/
class StubNuPkgToRssTransformer extends NuPkgToRssTransformer{

    protected NugetContext getContext() {
        throw new UnsupportedOperationException("Тестовый класс не поддерживает этот метод");
    }
}

