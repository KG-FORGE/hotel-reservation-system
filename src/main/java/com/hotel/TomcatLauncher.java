package com.hotel;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Embedded Apache Tomcat 10.1 Launcher.
 * Allows running the full Jakarta EE 10 Servlet/JSP application locally
 * without requiring an external Tomcat installation.
 */
public class TomcatLauncher {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portProp = System.getProperty("server.port");
        if (portProp == null) {
            portProp = System.getenv("PORT");
        }
        if (portProp != null && !portProp.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portProp.trim());
            } catch (NumberFormatException ignored) {}
        }

        String webappDir = new File("src/main/webapp").getAbsolutePath();
        File baseDir = new File("target/tomcat");
        baseDir.mkdirs();

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        tomcat.setPort(port);
        tomcat.getConnector(); // Initialize default HTTP connector

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDir);
        ctx.setReloadable(true);
        // Ensure webapp delegates to the launcher classloader (contains tomcat embed classes)
        ctx.setParentClassLoader(TomcatLauncher.class.getClassLoader());

        // Declare alternative location for classes to allow JSP compiler and annotations to find compiled classes
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        System.out.println("================================================================");
        System.out.println("   KG GRAND HOTEL RESERVATION SYSTEM - TOMCAT 10.1 SERVER");
        System.out.println("================================================================");
        System.out.println("Server running at:  http://localhost:" + port + "/");
        System.out.println("Admin Portal at:    http://localhost:" + port + "/admin/login");
        System.out.println("Default Admin:      admin / Admin@Hotel2026");
        System.out.println("Data Directory:     " + new File("data").getAbsolutePath());
        System.out.println("================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
