package ru.aristar.jnuget;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.rss.EntryProperties;
import ru.aristar.jnuget.rss.PackageEntry;
import ru.aristar.jnuget.rss.PackageEntryNameComparator;
import ru.aristar.jnuget.rss.PackageFeed;

/**
 *
 * @author sviridov
 */
public class NuPkgToRssTransformer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NugetContext context;

    public NuPkgToRssTransformer(NugetContext context) {
        this.context = context;
    }

    public PackageFeed transform(Collection<NupkgFile> files, String orderBy, String skip, String top) {
        PackageFeed feed = new PackageFeed();
        feed.setId(context.getRootUri().toString());
        feed.setUpdated(new Date());
        feed.setTitle("Packages");
        ArrayList<PackageEntry> packageEntrys = new ArrayList<>();

        for (NupkgFile nupkg : files) {
            try {
                PackageEntry entry = context.createPackageEntry(nupkg);
                entry.getProperties().setIsLatestVersion(Boolean.TRUE);
                addServerInformationInToEntry(entry);
                packageEntrys.add(entry);
            } catch (IOException | NoSuchAlgorithmException e) {
                logger.warn("Ошибка сбора информации о пакете", e);
            }
        }
        Collections.sort(packageEntrys, new PackageEntryNameComparator());
        feed.setEntries(packageEntrys);
        return feed;
    }

    private void addServerInformationInToEntry(PackageEntry entry) {
        EntryProperties properties = entry.getProperties();
        //TODO Не факт, что сюда
        //****************************
        properties.setIconUrl("");
        properties.setLicenseUrl("");
        properties.setProjectUrl("");
        properties.setReportAbuseUrl("");
        //***************************
        properties.setDownloadCount(-1);
        properties.setVersionDownloadCount(-1);
        properties.setRatingsCount(0);
        properties.setVersionRatingsCount(0);
        properties.setRating(Double.valueOf(0));
        properties.setVersionRating(Double.valueOf(0));
    }
}
