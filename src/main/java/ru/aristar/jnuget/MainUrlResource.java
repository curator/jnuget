package ru.aristar.jnuget;

import com.sun.jersey.multipart.FormDataParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import javax.security.auth.login.LoginException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.NugetFormatException;
import ru.aristar.jnuget.files.Nupkg;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.rss.MainUrl;
import ru.aristar.jnuget.rss.NuPkgToRssTransformer;
import ru.aristar.jnuget.rss.PackageFeed;
import ru.aristar.jnuget.security.Role;
import ru.aristar.jnuget.sources.PackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Сервис управления пакетами
 *
 * @author sviridov
 */
@Path("{storageName}")
public class MainUrlResource {

    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Контекст публикации сервиса
     */
    @Context
    private UriInfo context;

    /**
     * Имя хранилища
     */
    @PathParam("storageName")
    private String storageName;

    /**
     * Конструктор по умолчанию (требование JAX-RS)
     */
    public MainUrlResource() {
    }

    /**
     * Возвращает XML корневого узла хранилища
     *
     * @return XML
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("")
    public Response getXml() {
        MainUrl mainUrl = new MainUrl(context.getAbsolutePath().toString());
        XmlStreamingOutput streamingOutput = new XmlStreamingOutput(mainUrl);
        return Response.ok(streamingOutput, MediaType.APPLICATION_XML).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{metadata : [$]metadata}")
    public Response getMetadata() throws IOException {
        InputStream inputStream = MainUrlResource.class.getResourceAsStream("/metadata.xml");
        ResponseBuilder response = Response.ok((Object) inputStream);
        return response.build();
    }

    /**
     * Возвращает HTML ответ с RSS содержащей информацию о пакетах
     *
     * @param filter условие выборки пакетов
     * @param orderBy порядок сортировки пакетов (по умолчанию по дате
     * публикации)
     * @param skip количество пакетов, которое необходимо пропустить (по
     * умолчанию 0)
     * @param top количество пакетов в выборке
     * @param searchTerm условие поиска
     * @param targetFramework фрейморк, для которого предназначен пакет
     * @return HTML c RSS
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{packages : (Packages)[(]?[)]?|(Search)[(][)]}")
    public Response getPackages(@QueryParam("$filter") String filter,
            @QueryParam("$orderby") @DefaultValue("updated") String orderBy,
            @QueryParam("$skip") @DefaultValue("0") int skip,
            @QueryParam("$top") @DefaultValue("-1") int top,
            @QueryParam("searchTerm") String searchTerm,
            @QueryParam("targetFramework") String targetFramework) {
        try {
            logger.debug("Запрос пакетов из хранилища {}: filter={}, orderBy={}, skip={}, "
                    + "top={}, searchTerm={}, targetFramework={}",
                    new Object[]{storageName, filter, orderBy, skip, top, searchTerm, targetFramework});
            PackageFeed feed = getPackageFeed(filter, searchTerm, targetFramework, orderBy, skip, top);
            XmlStreamingOutput streamingOutput = new XmlStreamingOutput(feed);
            return Response.ok(streamingOutput, MediaType.APPLICATION_ATOM_XML_TYPE).build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка получения списка пакетов";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    /**
     * Возвращает количество пакетов
     *
     * @param filter условие выборки пакетов
     * @param searchTerm условие поиска
     * @param targetFramework фрейморк, для которого предназначен пакет
     * @return TEXT c числом пакетов
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{packages : (Packages)[(]?[)]?|(Search)[(][)]}/{count : [$]count}")
    public Response getPackageCount(@QueryParam("$filter") String filter,
            @QueryParam("searchTerm") String searchTerm,
            @QueryParam("targetFramework") String targetFramework) {
        try {
            logger.debug("Запрос количества пакетов из хранилища {}: filter={}, searchTerm={}, targetFramework={}",
                    new Object[]{storageName, filter, searchTerm, targetFramework});
            Collection<? extends Nupkg> files = getPackages(filter, searchTerm, targetFramework);
            final int count = files.size();
            logger.debug("Получено {} пакетов", new Object[]{count});
            return Response.ok(Integer.toString(count), MediaType.TEXT_PLAIN).build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка получения списка пакетов";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    /**
     * Получения потока с данными пакета
     *
     * @param id идентификатор пакета
     * @param versionString строка версии пакета
     * @return поток с файлом пакета
     */
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("download/{id}/{version}")
    public Response getPackage(@PathParam("id") String id,
            @PathParam("version") String versionString) {
        try {
            Version version = Version.parse(versionString);
            PackageSource packageSource = getPackageSource();
            Nupkg nupkg = packageSource.getPackage(id, version);
            if (nupkg == null) {
                logger.warn("Пакет " + id + ":" + versionString + " не найден");
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            InputStream inputStream = nupkg.getStream();
            ResponseBuilder response = Response.ok((Object) inputStream);
            response.header(HttpHeaders.CONTENT_LENGTH, nupkg.getSize());
            response.type(MediaType.APPLICATION_OCTET_STREAM);
            String fileName = nupkg.getFileName();
            response.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            return response.build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка получения пакета " + id + " " + versionString;
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("downloadPart/{storage}/{id}/{version}")
    public Response getPackagePart(
            @PathParam("storage") int storageId,
            @PathParam("id") String id,
            @PathParam("version") String versionString) {
        return Response.serverError().entity("Не реализовано").build();
    }

    /**
     * Метод помещения пакета в хранилище для версии NuGet старше 1.6
     *
     * @param apiKey ключ доступа
     * @param inputStream поток данных
     * @return ответ сервера (CREATED или FORBIDDEN)
     */
    @PUT
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response putPackage(@HeaderParam(API_KEY_HEADER_NAME) String apiKey,
            @FormDataParam("package") InputStream inputStream) {
        return pushPackage(apiKey, inputStream, Response.Status.CREATED);
    }

    /**
     * Метод помещения в хранилище для версии NuGet младше 1.6
     *
     * @param apiKey ключ доступа
     * @param inputStream поток данных
     * @return ответ сервера (OK или FORBIDDEN)
     */
    @POST
    @Path("PackageFiles/{apiKey}/nupkg")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response postPackage(@PathParam("apiKey") String apiKey, InputStream inputStream) {
        return pushPackage(apiKey, inputStream, Response.Status.OK);
    }

    @POST
    @Path("PublishedPackages/Publish")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPackageMetadata(InputStream inputStream) {
        //JSONJAXBContext context = JSONJAXBContext.newInstance(classesToBeBound);
        //TODO необходимо обработать запрос
        //{"id":"Neolant.IOT.EventBus","key":"4003d786-cc37-4004-bfdf-c4f3e8ef9b3a","version":"0.0.2.557"}
        ResponseBuilder response = Response.ok();
        return response.build();
    }

    /**
     * Удаление пакета из хранилища для NuGet версии младше 1.6
     *
     * @param apiKey ключ доступа
     * @param packageId идентификатор пакета
     * @param versionString версия пакета
     * @return ответ сервера
     */
    @DELETE
    @Path("Packages/{apiKey}/{packageId}/{versionString}")
    public Response deletePackageOld(
            @PathParam("apiKey") String apiKey,
            @PathParam("packageId") String packageId,
            @PathParam("versionString") String versionString) {
        return deletePackage(apiKey, packageId, versionString);
    }

    /**
     * Удаление пакета из хранилища для NuGet версии 1.6 и старше
     *
     * @param apiKey ключ доступа
     * @param packageId идентификатор пакета
     * @param versionString версия пакета
     * @return ответ сервера
     */
    @DELETE
    @Path("{packageId}/{versionString}")
    public Response deletePackageNew(
            @HeaderParam(API_KEY_HEADER_NAME) String apiKey,
            @PathParam("packageId") String packageId,
            @PathParam("versionString") String versionString) {

        return deletePackage(apiKey, packageId, versionString);

    }

    /**
     * Удаление пакета из хранилища
     *
     * @param apiKey ключ доступа
     * @param packageId идентификатор пакета
     * @param versionString версия пакета
     * @return результат удаления
     */
    private Response deletePackage(String apiKey,
            String packageId,
            String versionString) {
        NugetContext nugetContext = new NugetContext(context.getBaseUri());
        try {
            nugetContext.login(apiKey);
            try {
                if (nugetContext.isUserInRole(Role.Delete)) {
                    Version version = Version.parse(versionString);
                    PackageSource<Nupkg> packageSource = getPackageSource();
                    Nupkg nupkg = packageSource.getPackage(packageId, version);
                    if (nupkg == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                    packageSource.removePackage(nupkg);
                    return Response.status(Response.Status.OK).build();
                } else {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            } finally {
                nugetContext.logout();
            }
        } catch (LoginException e) {
            logger.warn("Не достаточно прав на удаление пакета " + e.getLocalizedMessage());
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (NugetFormatException e) {
            logger.warn("Не корректная версия пакета " + e.getLocalizedMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * @return источник пакетов
     */
    private PackageSource<Nupkg> getPackageSource() {
        return PackageSourceFactory.getInstance().getPackageSource();
    }

    /**
     * Помещает пакет в хранилище
     *
     * @param apiKey ключ доступа
     * @param inputStream поток данных
     * @param correctStatus статус, ожидаемый в случае удачного помещения пакета
     * @return FORBIDDEN или "correctStatus"
     */
    private Response pushPackage(String apiKey, InputStream inputStream, Response.Status correctStatus) {
        try {
            logger.debug("Получен пакет ApiKey={}", new Object[]{apiKey});
            NugetContext nugetContext = new NugetContext(context.getBaseUri());
            nugetContext.login(apiKey);
            if (nugetContext.isUserInRole(Role.Push)) {
                ResponseBuilder response;
                try (TempNupkgFile nupkgFile = new TempNupkgFile(inputStream)) {
                    logger.debug("Помещение пакета {} версии {} в хранилище",
                            new Object[]{nupkgFile.getId(), nupkgFile.getVersion()});
                    boolean pushed = getPackageSource().pushPackage(nupkgFile);

                    if (pushed) {
                        response = Response.status(correctStatus);
                    } else {
                        logger.debug("Публикация пакета в хранилище не произведена ApiKey={}", new Object[]{apiKey});
                        response = Response.status(Response.Status.FORBIDDEN);
                    }
                } finally {
                    nugetContext.logout();
                }
                return response.build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } catch (LoginException e) {
            final String errorMessage = "Недостаточно прав для помещения пакета в хранилище";
            logger.warn(errorMessage + " " + e.getLocalizedMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessage).build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка помещения пакета в хранилище";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }

    }

    /**
     * Возвращает объектную реализацию RSS рассылки с пакетами
     *
     * @param filter условие фильтрации
     * @param searchTerm условие поиска
     * @param targetFramework список фреймворков, для которых предназначен пакет
     * @param orderBy условие упорядочивания
     * @param skip количество пропускаемых записей
     * @param top количество возвращаемых записей
     * @return объектное представление RSS
     */
    private PackageFeed getPackageFeed(String filter, String searchTerm, String targetFramework, String orderBy, int skip, int top) {
        //Получить пакеты
        Collection<? extends Nupkg> files = getPackages(filter, searchTerm, targetFramework);
        logger.debug("Получено {} пакетов", new Object[]{files.size()});
        //Преобразовать пакеты в RSS
        NugetContext nugetContext = new NugetContext(context.getBaseUri());
        NuPkgToRssTransformer toRssTransformer = nugetContext.createToRssTransformer();
        PackageFeed feed = toRssTransformer.transform(files, orderBy, skip, top);
        return feed;
    }

    /**
     * Возвращает коллекцию пакетов, соответствующую условиям поиска
     *
     * @param filter условие фильтрации
     * @param searchTerm условие поиска
     * @param targetFramework список фреймворков, для которых предназначен пакет
     * @return коллекция пакетов
     */
    private Collection<? extends Nupkg> getPackages(String filter, String searchTerm, String targetFramework) {
        //Получить источник пакетов
        PackageSource<Nupkg> packageSource = getPackageSource();
        //Выбрать пакеты по запросу
        QueryExecutor queryExecutor = new QueryExecutor();
        Collection<? extends Nupkg> files = queryExecutor.execQuery(packageSource, filter, searchTerm, targetFramework);
        return files;
    }
    /**
     * Имя заголовка запроса с ключем доступа
     */
    public static final String API_KEY_HEADER_NAME = "X-NuGet-ApiKey";
}
