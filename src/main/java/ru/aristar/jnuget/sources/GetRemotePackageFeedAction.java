package ru.aristar.jnuget.sources;

import java.util.List;
import java.util.concurrent.RecursiveAction;
import ru.aristar.jnuget.client.NugetClient;
import ru.aristar.jnuget.files.RemoteNupkg;

/**
 * Задача получения списка пактов из удаленного хранилища
 *
 * @author sviridov
 */
public class GetRemotePackageFeedAction extends RecursiveAction {

    private static final int PACKAGES_PER_THREAD = 1000;
    private final List<RemoteNupkg> packages;
    private final int low;
    private final int top;
    private final NugetClient client;

    public GetRemotePackageFeedAction(List<RemoteNupkg> packages, int low, int top, NugetClient client) {
        this.packages = packages;
        this.low = low;
        this.top = top;
        this.client = client;
    }

    @Override
    protected void compute() {
        if (top - low < PACKAGES_PER_THREAD) {
            loadPackages();
        } else {
            final int middle = (top - low) / 2;
            GetRemotePackageFeedAction bottomAction = new GetRemotePackageFeedAction(packages, low, middle, client);
            GetRemotePackageFeedAction topAction = new GetRemotePackageFeedAction(packages, middle, top, client);
            bottomAction.fork();
            topAction.fork();
            invokeAll(bottomAction, topAction);
        }
    }

    private void loadPackages() {
    }
}
