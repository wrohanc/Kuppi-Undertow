package com.ro.learn;


import com.ro.learn.push.PushEndPoint;
import com.ro.learn.resource.JAXRSConfig;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.ServletException;

public class UndertowServer {
    private static Undertow server;
    private static DeploymentManager deploymentManager;
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        start(DEFAULT_HOST, DEFAULT_PORT);
    }

    private static void start(final String host, final int port) {
        PathHandler path = Handlers.path();

        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(path)
                .build();

        server.start();

        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(UndertowServer.class.getClassLoader())
                .setContextPath("/")
                .addWelcomePage("index.html")
                //.addListeners(listener(Listener.class))
                .setResourceManager(new ClassPathResourceManager(UndertowServer.class.getClassLoader()))
                .addServlets(
                        Servlets.servlet("jerseyServlet", ServletContainer.class)
                                .setLoadOnStartup(1)
                                .addInitParam("javax.ws.rs.Application", JAXRSConfig.class.getName())
                                .addMapping("/api/*"))
                .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME,
                        new WebSocketDeploymentInfo()
                                .setBuffers(new DefaultByteBufferPool(true, 100))
                                .addEndpoint(PushEndPoint.class))
                .setDeploymentName("UndertowServer.war");

        deploymentManager = Servlets.defaultContainer().addDeployment(servletBuilder);
        deploymentManager.deploy();

        try {
            path.addPrefixPath("/", deploymentManager.start());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
