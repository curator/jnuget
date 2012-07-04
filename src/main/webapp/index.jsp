<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="java.util.jar.Manifest"%>
<%@page import="java.io.InputStream"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>NuGet Private Repository</title>
        <style type="text/css">
            body { font-family: Calibri; }
        </style>
    </head>
    <body>
        <div>
            <%                
                String vesion = null;
                try {
                    InputStream inputStream = getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
                    if (inputStream != null) {
                        Manifest manifest = new Manifest(inputStream);
                        vesion = manifest.getMainAttributes().getValue("Implementation-Version");
                    }
                } catch (Exception e) {
                    LoggerFactory.getLogger("Index.page").warn("Ошибка определения версии пакета", e);
                }
                vesion = vesion == null ? "" : "v" + vesion;%>
            <h2>You are running JNuGet.Server <%=vesion%> </h2>
            <p>
                Click <a href="nuget/nuget/Packages">here</a> to view your packages.
            </p>
            <p>
                Click <a href="sourceManager.xhtml">here</a> to view manager.
            </p>
            <fieldset style="width:800px">
                <legend><strong>Repository URL's</strong></legend>
                In the package manager settings, add the following URL to the list of 
                Package Sources:
                <blockquote>
                    <strong><%=request.getRequestURL()%>nuget/nuget</strong>

                </blockquote>

                Use the command below to push packages to this feed using the nuget command line tool (nuget.exe).

                <blockquote>
                    <strong>nuget push {package file} -s <%=request.getRequestURL()%>nuget/ {apikey}</strong>
                </blockquote>            
            </fieldset>
        </div>
    </body>
</html>
