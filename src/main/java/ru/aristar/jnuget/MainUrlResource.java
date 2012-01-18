package ru.aristar.jnuget;

import ru.aristar.jnuget.sources.PackageSourceFactory;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import java.io.*;
import java.util.Collection;
import java.util.Date;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.rss.MainUrl;
import ru.aristar.jnuget.rss.PackageFeed;
import ru.aristar.jnuget.sources.FilePackageSource;

/**
 * REST Web Service
 *
 * @author sviridov
 */
@Path("")
public class MainUrlResource {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MainUrlResource
     */
    public MainUrlResource() {
    }

    /**
     * Retrieves representation of an instance of
     * ru.aristar.jnuget.MainUrlResource
     *
     * @return
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
            //Преобразовать пакеты в RSS
            NuPkgToRssTransformer toRssTransformer = nugetContext.createToRssTransformer();
            PackageFeed feed = toRssTransformer.transform(files, orderBy, skip, top);
            return Response.ok(feed.getXml(), MediaType.APPLICATION_ATOM_XML_TYPE).build();
        } catch (JAXBException e) {
            final String errorMessage = "Ошибка преобразования XML";
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
            InputStream inputStream = nupkg.getStream();
            ResponseBuilder response = Response.ok((Object) inputStream);
            response.type(MediaType.APPLICATION_OCTET_STREAM);
            String fileName = nupkg.getFileName();
            response.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            return response.build();
        } catch (Exception e) {
            final String errorMessage = "Ошибка преобразования XML";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    /**
     * PUT method for updating or creating an instance of MainUrlResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response putXml(@HeaderParam("X-NuGet-ApiKey") String apiKey,
            @FormDataParam("package") InputStream inputStream,
            @FormDataParam("package") FormDataContentDisposition fileInfo) throws IOException, JAXBException, SAXException {
        logger.debug("Получен пакет: {} ApiInfo={}", new Object[]{fileInfo.getFileName(), apiKey});
        NupkgFile nupkgFile = new NupkgFile(inputStream, new Date());
        getPackageSource().pushPackage(nupkgFile);
        ResponseBuilder response = Response.ok();
        return response.build();
    }

    private FilePackageSource getPackageSource() {
        return PackageSourceFactory.getInstance().getPackageSource();
    }
}
