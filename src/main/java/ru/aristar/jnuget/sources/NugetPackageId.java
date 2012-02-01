package ru.aristar.jnuget.sources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.aristar.jnuget.Version;
import ru.aristar.jnuget.files.ClassicNupkg;

/**
 * Информация о пакете.
 *
 * @author sviridov
 */
public class NugetPackageId {

    /**
     * Идентификатор пакета.
     */
    private String id;

    /**
     * Версия пакета.
     */
    private Version version;

    /**
     * Возвращает идентификатор пакета.
     * @return идентификатор пакета
     */
    public String getId() {
        return id;
    }
    
    /**
     * Устанавливает идентификатор пакета.
     * @param id идентификатор пакета
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Возвращает версию пакета.
     * @return версия пакета
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Устанавливает версию пакета.
     * @param version версия пакета
     */
    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format("%s.%s%s", id, version, ClassicNupkg.DEFAULT_EXTENSION);
    }
         
    /**
     * Выражение разбора строки имени файла
     */
    private static Pattern parser =
            Pattern.compile("^(.+?)\\.(" + Version.VERSION_FORMAT + ")" + ClassicNupkg.DEFAULT_EXTENSION + "$");

    /**
     * Разбирает строку названия файла пакета
     * @param filename название файла
     * @return информация о пакете
     * @throws NugetFormatException некорректный формат имени файла
     */
    public static NugetPackageId parse(String filename) throws NugetFormatException {
        if (filename == null || filename.isEmpty()) {
            throw new NugetFormatException("Неправильный формат строки " + filename);
        }
        Matcher matcher = parser.matcher(filename);
        if (!matcher.matches()) {
            throw new NugetFormatException("Неправильный формат строки " + filename);
        } else {
            try {
                NugetPackageId result = new NugetPackageId();
                result.id = matcher.group(1);
                result.version = Version.parse(matcher.group(2));
                return result;
            } catch (Exception ex) {
                throw new NugetFormatException("Неправильный формат строки", ex);
            }
        }
    }
}
