package ru.aristar.jnuget;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Path("")
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
                packageEntrys.add(entry);
            }
            Collections.sort(packageEntrys, new PackageEntryNameComparator());
            feed.setEntries(packageEntrys);
            //конец реализации
            return Response.ok(feed.getXml(), MediaType.APPLICATION_ATOM_XML_TYPE).build();
        } catch (JAXBException x) {
            final String errorMessage = "Ошибка преобразования XML";
            logger.error(errorMessage, x);
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
