package ru.aristar.jnuget.sources;

import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.sources.push.BeforeTrigger;
import ru.aristar.jnuget.sources.push.NugetPushException;

/**
 * Тестовый, ничего не делающий триггер
 */
public class TestBeforeTrigger implements BeforeTrigger {

    /**
     * Тестовое свойство
     */
    private int testProperty;

    /**
     * @return тестовое свойство
     */
    public int getTestProperty() {
        return testProperty;
    }

    /**
     * @param testProperty тестовое свойство
     */
    public void setTestProperty(int testProperty) {
        this.testProperty = testProperty;
    }

    @Override
    public boolean doAction(Nupkg nupkg, PackageSource<? extends Nupkg> packageSource) throws NugetPushException {
        return true;
    }
}
