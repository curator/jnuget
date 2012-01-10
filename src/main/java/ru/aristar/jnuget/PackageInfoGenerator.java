package ru.aristar.jnuget;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author sviridov
 */
class PackageInfoGenerator {

    private String getTitle() {
        return "title";
    }

    private String getAuthor() {
        return "Author";
    }

    private String getDescription() {
        return "description";
    }

    private List<SyndEntry> getEntries() {
        ArrayList<SyndEntry> entrys = new ArrayList<SyndEntry>();
        SyndEntry entry = new SyndEntryImpl();
        entry.setAuthor("Author");
        entry.setTitle("Title");
        entry.setLink("url");
        entry.setUri("url");
        entry.setPublishedDate(new Date());
        //entry.setCategories(Arrays.asList(categories));
        SyndContent description = new SyndContentImpl();
        description.setType("sdas");
        description.setValue("sdkjaldkjldkj");
        entry.setDescription(description);
        entrys.add(entry);
        return entrys;
    }

    private String getUrl() {
        return "http://url";
    }

    public enum SyndFeedType {

        RSS_09("rss_0.9"),
        RSS_091("rss_0.91"),
        RSS_092("rss_0.92"),
        RSS_093("rss_0.93"),
        RSS_O94("rss_0.94"),
        RSS_10("rss_1.0"),
        RSS_20("rss_2.0"),
        ATOM_03("atom_0.3"),
        ATOM_10("atom_1.0");
        private String code;

        private SyndFeedType(String code) {

            this.code = code;

        }

        public String getCode() {

            return code;

        }
    }

    public SyndFeed makeFeed() {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(getType());
        feed.setAuthor(getAuthor());
        feed.setCopyright(getAuthor());
        feed.setTitle(getTitle());
        feed.setDescription(getDescription());
        feed.setLink(getUrl());
        feed.setUri(getUrl());
        feed.setEntries(getEntries());
        return feed;
    }

    private String getType() {
        return SyndFeedType.ATOM_10.getCode();
    }
}
