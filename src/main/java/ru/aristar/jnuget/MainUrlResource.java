package ru.aristar.jnuget;

import com.sun.jersey.multipart.FormDataParam;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.files.TempNupkgFile;
import ru.aristar.jnuget.rss.MainUrl;
import ru.aristar.jnuget.rss.PackageFeed;
import ru.aristar.jnuget.sources.FilePackageSource;
import ru.aristar.jnuget.sources.PackageSourceFactory;

/**
 * Сервис управления пакетами
 *
 * @author sviridov
 */
@Path("")
public class MainUrlResource {

    /**
     * Логгер
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Context
    private UriInfo context;

    /**
     * Конструктор по умолчанию (требование JAX-RS)
     */
    public MainUrlResource() {
    }

    /**
     * Возвращает XML корневого узла сервера
     *
     * @return XML
     */
    @GET
    @Produces("application/xml")
    @Path("nuget")
    public Response getXml() {
        StringWriter writer = new StringWriter();
        try {
            MainUrl mainUrl = new MainUrl(context.getAbsolutePath().toString());
            mainUrl.writeXml(writer);
        } catch (JAXBException e) {
            final String errorMessage = "Ошибка преобразования XML";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
        return Response.ok(writer.toString(), MediaType.APPLICATION_XML).build();
    }

    @GET
    @Produces("application/xml")
    @Path("")
    public Response getRootXml() {
        //TODO Разобраться со структурой приложения (что по какому URL должно находится)
        return getXml();
    }

    @GET
    @Produces("application/xml")
    @Path("nuget/{metadata : [$]metadata}")
    public Response getMetadata() {
        InputStream inputStream = this.getClass().getResourceAsStream("/metadata.xml");
        ResponseBuilder response = Response.ok((Object) inputStream);
        return response.build();
    }

    @GET
    @Produces("application/xml")
    @Path("nuget/{packages : (Packages)[(]?[)]?|(Search)[(][)]}")
    public Response getPackages(@QueryParam("$filter") String filter,
            @QueryParam("$orderby") String orderBy,
            @QueryParam("$skip") @DefaultValue("0") int skip,
            @QueryParam("$top") @DefaultValue("-1") int top,
            @QueryParam("searchTerm") String searchTerm,
            @QueryParam("targetFramework") String targetFramework) {
        try {
            logger.debug("Запрос пакетов: filter={}, orderBy={}, skip={}, "
                    + "top={}, searchTerm={}, targetFramework={}",
                    new Object[]{filter, orderBy, skip, top, searchTerm, targetFramework});
            NugetContext nugetContext = new NugetContext(context.getBaseUri());
            //Получить источник пакетов
            FilePackageSource packageSource = getPackageSource();
            //Выбрать пакеты по запросу
            QueryExecutor queryExecutor = new QueryExecutor();
            Collection<NupkgFile> files = queryExecutor.execQuery(packageSource, filter);
            logger.debug("Получено {} пакетов", new Object[]{files.size()});
            //Преобразовать пакеты в RSS
            NuPkgToRssTransformer toRssTransformer = nugetContext.createToRssTransformer();
            PackageFeed feed = toRssTransformer.transform(files, orderBy, skip, top);
            return Response.ok(feed.getXml(), MediaType.APPLICATION_ATOM_XML_TYPE).build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка получения списка пакетов";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("nuget/{packages : (Packages)[(]?[)]?|(Search)[(][)]}/{count : [$]count}")
    public Response getPackageCount(@QueryParam("$filter") String filter,
            @QueryParam("$orderby") String orderBy,
            @QueryParam("$skip") @DefaultValue("0") int skip,
            @QueryParam("$top") @DefaultValue("-1") int top,
            @QueryParam("searchTerm") String searchTerm,
            @QueryParam("targetFramework") String targetFramework) {
        try {
            //TODO Решиние "В лоб" необходимо переделать по человечески (вынести получение пакетов в отдельный метод)
            logger.debug("Запрос количества пакетов: filter={}, orderBy={}, skip={}, "
                    + "top={}, searchTerm={}, targetFramework={}",
                    new Object[]{filter, orderBy, skip, top, searchTerm, targetFramework});
            NugetContext nugetContext = new NugetContext(context.getBaseUri());
            //Получить источник пакетов
            FilePackageSource packageSource = getPackageSource();
            //Выбрать пакеты по запросу
            QueryExecutor queryExecutor = new QueryExecutor();
            Collection<NupkgFile> files = queryExecutor.execQuery(packageSource, filter);
            logger.debug("Получено {} пакетов", new Object[]{files.size()});
            //Преобразовать пакеты в RSS
            NuPkgToRssTransformer toRssTransformer = nugetContext.createToRssTransformer();
            PackageFeed feed = toRssTransformer.transform(files, orderBy, skip, top);
            return Response.ok(Integer.toString(feed.getEntries().size()), MediaType.TEXT_PLAIN).build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка получения списка пакетов";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("download/{id}/{version}")
    public Response getPackage(@PathParam("id") String id,
            @PathParam("version") String versionString) {
        try {
            Version version = Version.parse(versionString);
            FilePackageSource packageSource = getPackageSource();
            NupkgFile nupkg = packageSource.getPackage(id, version);
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

    /**
     * Метод помещения пакета в хранилище для версии NuGet старше 1.6
     *
     * @param apiKey ключ доступа
     * @param inputStream поток данных
     * @return
     */
    @PUT
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response putPackage(@HeaderParam("X-NuGet-ApiKey") String apiKey,
            @FormDataParam("package") InputStream inputStream) {
        try {
            logger.debug("Получен пакет ApiKey={}", new Object[]{apiKey});
            ResponseBuilder response;
            try (TempNupkgFile nupkgFile = new TempNupkgFile(inputStream)) {
                boolean pushed = getPackageSource().pushPackage(nupkgFile, apiKey);
                if (pushed) {
                    response = Response.status(Response.Status.CREATED);
                } else {
                    response = Response.status(Response.Status.FORBIDDEN);
                }
            }
            return response.build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка помещения пакета в хранилище";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    @POST
    @Path("PackageFiles/{apiKey}/nupkg")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response postPackage(@PathParam("apiKey") String apiKey, InputStream inputStream) throws IOException {
        try {
            logger.debug("Получен пакет ApiKey={}", new Object[]{apiKey});
            ResponseBuilder response;
            try (TempNupkgFile nupkgFile = new TempNupkgFile(inputStream)) {
                boolean pushed = getPackageSource().pushPackage(nupkgFile, apiKey);
                if (pushed) {
                    response = Response.ok();
                } else {
                    response = Response.status(Response.Status.FORBIDDEN);
                }
            }
            return response.build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка помещения пакета в хранилище";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    @POST
    @Path("PublishedPackages/Publish")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPackageMetadata(InputStream inputStream) {
        //TODO необходимо обработать запрос
        //{"id":"Neolant.IOT.EventBus","key":"4003d786-cc37-4004-bfdf-c4f3e8ef9b3a","version":"0.0.2.557"}
        ResponseBuilder response = Response.ok();
        return response.build();
    }

    private FilePackageSource getPackageSource() {
        return PackageSourceFactory.getInstance().getPackageSource();
    }
}
