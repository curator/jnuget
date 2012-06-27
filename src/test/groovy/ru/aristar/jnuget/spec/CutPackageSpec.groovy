package ru.aristar.jnuget.spec


import spock.lang.Specification
import ru.aristar.jnuget.rss.NuPkgToRssTransformer
import ru.aristar.jnuget.rss.NuPkgToRssTransformerTest.NuPkgToRssTransformerNoContext

/**
 *
 * @author sviridov
 */
class CutPackageSpec extends Specification{

    /**
    * Создание тестового списка объектов
    * @param size желаемый размер списка
    * @return список объектов
    */
    List<Object> testSource(int size){
        List<Object> sources = new ArrayList<Object>();
        size.times { sources.add(it + 1) }
        return sources;
    }

    /**
    * Список объектов должен корректно обрезаться
    */
    def "Object list must correct cutting"(){
        setup:
        NuPkgToRssTransformer transformer = new NuPkgToRssTransformerNoContext();
        expect:
        transformer.cutPackageList(skip, top, testSource(sourceSize)).size() == resultSize
        where:
        sourceSize  | skip  |  top  | resultSize
            0       |   0   |   0   |   0
            30      |   30  |   0   |   0
            10      |   0   |   -1  |   10
            60      |   30  |   -1  |   30
    }
}

