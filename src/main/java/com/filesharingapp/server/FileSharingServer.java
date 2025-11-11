package com.filesharingapp.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * FileSharingServer
 * -----------------
 * Embedded Jetty 11 HTTP server for the File Sharing App.
 * - Serves static files (index.html) under /web
 * - Provides /health, /handshake, and /api/send-http endpoints
 * - Fully compatible with Jakarta Servlet 5 (Jetty 11)
 */
public class FileSharingServer {

    private static final Logger log = Logger.getLogger(FileSharingServer.class.getName());
    private final int port;

    /**
     * Constructor.
     * @param port Port to listen on.
     */
    public FileSharingServer(int port) {
        this.port = port;
    }

    /**
     * Start the embedded Jetty server and mount all handlers.
     */
    public void start() throws Exception {
        // Create Jetty server instance
        Server server = new Server(port);

        // Serve static resources from src/main/resources/web
        ResourceHandler staticHandler = new ResourceHandler();
        staticHandler.setWelcomeFiles(new String[]{"index.html"});
        staticHandler.setResourceBase(
                FileSharingServer.class.getClassLoader()
                        .getResource("web")
                        .toExternalForm()
        );

        // Servlet context for APIs
        ServletContextHandler apiContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        apiContext.setContextPath("/");

        // Register servlets
        apiContext.addServlet(new ServletHolder(new HealthServlet()), "/health");
        apiContext.addServlet(new ServletHolder(new HandshakeServlet()), "/handshake");
        apiContext.addServlet(new ServletHolder(new HttpSendServlet()), "/api/send-http");

        // Combine static + API handlers
        HandlerList handlers = new HandlerList();
        handlers.addHandler(staticHandler);
        handlers.addHandler(apiContext);
        server.setHandler(handlers);

        log.info("[Server] Starting FileSharingServer on port " + port + "...");
        server.start();
        log.info("[Server] Started successfully. Open http://localhost:" + port + "/ in your browser.");
        try {
            String url = "http://localhost:" + port + "/";
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            log.info("[Server] ✅ Opened browser at " + url);
        } catch (Exception ex) {
            log.warning("[Server] Could not open browser automatically: " + ex.getMessage());
        }
        server.join();
    }

    // -------------------------------------------------------------
    // 1️⃣ /health endpoint
    // -------------------------------------------------------------
    public static class HealthServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"OK\",\"time\":\"" + LocalDateTime.now() + "\"}");
        }
    }

    // -------------------------------------------------------------
    // 2️⃣ /handshake endpoint
    // -------------------------------------------------------------
    public static class HandshakeServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String name = sanitize(req.getParameter("name"));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"handshake\":\"hello\",\"receiver\":\"" + name + "\"}");
        }
    }

    // -------------------------------------------------------------
    // 3️⃣ /api/send-http endpoint
    // -------------------------------------------------------------
    @MultipartConfig
    public static class HttpSendServlet extends HttpServlet {
        private static final File RECEIVED_DIR = new File("received");

        static {
            if (!RECEIVED_DIR.exists() && !RECEIVED_DIR.mkdirs()) {
                Logger.getLogger(HttpSendServlet.class.getName())
                        .warning("[Server] Could not create 'received' folder.");
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException, ServletException {

            String senderName = sanitize(req.getParameter("name"));
            String role = sanitize(req.getParameter("role"));
            String method = sanitize(req.getParameter("method"));

            if (!"Sender".equalsIgnoreCase(role) || !"HTTP".equalsIgnoreCase(method)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Only Sender + HTTP transfers are supported here.");
                return;
            }

            Collection<Part> parts = req.getParts();
            if (parts == null || parts.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("No files received.");
                return;
            }

            int savedCount = 0;
            for (Part part : parts) {
                String fileName = part.getSubmittedFileName();
                if (fileName == null || fileName.isBlank()) continue;

                if (!isAllowedFile(fileName)) {
                    log.warning("[Server] Rejected unsupported file: " + fileName);
                    continue;
                }

                File dest = new File(RECEIVED_DIR, System.currentTimeMillis() + "_" + fileName);
                try (InputStream in = part.getInputStream()) {
                    Files.copy(in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                log.info("[Server] Received file from '" + senderName + "': " + dest.getAbsolutePath());
                savedCount++;
            }

            if (savedCount == 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("No valid files were saved.");
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Successfully saved " + savedCount + " file(s).");
            }
        }

        /** Allowed file extensions for uploads */
        private boolean isAllowedFile(String fileName) {
            String lower = fileName.toLowerCase();
            return lower.endsWith(".zip") || lower.endsWith(".jpg")
                    || lower.endsWith(".jpeg") || lower.endsWith(".png");
        }
    }

    /** Utility: basic string sanitizer */
    private static String sanitize(String v) {
        if (v == null) return "";
        return v.replace("\"", "").trim();
    }
}
