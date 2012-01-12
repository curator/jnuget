package ru.aristar.jnuget;

import ru.aristar.jnuget.rss.MainUrl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
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
import ru.aristar.jnuget.files.NupkgFile;
import ru.aristar.jnuget.rss.PackageEntry;
import ru.aristar.jnuget.rss.PackageEntryNameComparator;
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
    @Path("nuget/Packages")
    public Response getPackages() {
        try {
            //Фейковая реализация
            File file = new File("c:/inetpub/wwwroot/nuget/Packages/");
            FilePackageSource packageSource = new FilePackageSource(file);
            PackageFeed feed = new PackageFeed();
            feed.setId(context.getAbsolutePath().toString());
            feed.setUpdated(new Date());
            feed.setTitle("Packages");
            ArrayList<PackageEntry> packageEntrys = new ArrayList<>();
            for (NupkgFile nupkg : packageSource.getPackages()) {
                PackageEntry entry = new PackageEntry(nupkg);
                entry.setRootUri(context.getPath());
                packageEntrys.add(entry);
            }
            Collections.sort(packageEntrys, new PackageEntryNameComparator());
            feed.setEntries(packageEntrys);
            //конец реализации
            return Response.ok(feed.getXml(), MediaType.APPLICATION_ATOM_XML_TYPE).build();
        } catch (JAXBException e) {
            final String errorMessage = "Ошибка преобразования XML";
            logger.error(errorMessage, e);
            return Response.serverError().entity(errorMessage).build();
        }
    }

    @GET
    @Produces("application/xml")
    @Path("nuget/{search : Search[(][)]}")
    //Search()?$filter=IsLatestVersion&$orderby=Id&$skip=0&$top=30&searchTerm=''&targetFramework=''
    public Response search(@QueryParam("$filter") String filter,
            @QueryParam("$orderby") String orderBy,
            @QueryParam("$skip") String skip,
            @QueryParam("searchTerm") String searchTerm,
            @QueryParam("targetFramework") String targetFramework) {
        try {
            //Фейковая реализация
            File file = new File("c:/inetpub/wwwroot/nuget/Packages/");
            FilePackageSource packageSource = new FilePackageSource(file);
            PackageFeed feed = new PackageFeed();
            feed.setId(context.getAbsolutePath().toString());
            feed.setUpdated(new Date());
            feed.setTitle("Search");
            ArrayList<PackageEntry> packageEntrys = new ArrayList<>();
            for (NupkgFile nupkg : packageSource.getPackages()) {
                PackageEntry entry = new PackageEntry(nupkg);
                entry.setRootUri(context.getPath());
                packageEntrys.add(entry);
            }
            Collections.sort(packageEntrys, new PackageEntryNameComparator());
            feed.setEntries(packageEntrys);
            //конец реализации
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
            //Фейковая реализация
            File file = new File("c:/inetpub/wwwroot/nuget/Packages/");
            FilePackageSource packageSource = new FilePackageSource(file);
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
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
