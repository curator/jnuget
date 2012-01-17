package ru.aristar.jnuget.sources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.NupkgFile;

/**
 * Информация о пакете.
 *
 * @author sviridov
 */
public class NugetPackageId {

    private String id;
    private Version version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
    private static Pattern parser =
            Pattern.compile("^(.+?)\\.(" + Version.VERSION_FORMAT + ")" + NupkgFile.DEFAULT_EXTENSION + "$");

    public static NugetPackageId Parse(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        Matcher matcher = parser.matcher(filename);
        if (!matcher.matches()) {
            return null;
        } else {
            try {
                NugetPackageId result = new NugetPackageId();
                result.id = matcher.group(1);
                result.version = Version.parse(matcher.group(2));
                return result;
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
