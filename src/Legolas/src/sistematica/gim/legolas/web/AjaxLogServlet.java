package sistematica.gim.legolas.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.velocity.app.VelocityEngine;
import sistematica.gim.legolas.Configuration;
import sistematica.gim.legolas.scheduler.Scheduler;

/**
 * Servlet richiamata via Ajax dalla pagina del log che invia le ultime
 * righe del file (parametro lines) o tutto il file (senza parametro).
 */
public class AjaxLogServlet extends BaseServlet {

    public AjaxLogServlet(Configuration configuration, VelocityEngine velocity, Scheduler scheduler) {
        super(configuration, velocity, scheduler);
    }

    private void tail(File src, Writer writer, int maxLines) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(src));
        String[] lines = new String[maxLines];
        int lastNdx = 0;
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (lastNdx == lines.length) {
                lastNdx = 0;
            }
            lines[lastNdx++] = line;
        }

        for (int ndx = lastNdx; ndx != lastNdx - 1; ndx++) {
            if (ndx == lines.length) {
                ndx = 0;
            }
            writer.write(lines[ndx]);
            writer.write("\n");
        }

        writer.flush();
    }

    private void writeAll(File src, Writer writer) throws IOException {
        Scanner scanner = new Scanner(src);
        String newline = System.getProperty("line.separator");
        try {
            while (scanner.hasNextLine()) {
                writer.write(scanner.nextLine() + newline);
            }
        } finally {
            scanner.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("password") != null) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);

            File logFile = new File(configuration.LOG_FILE);
            Object param = request.getParameter("lines");
            if (param != null) {
                int lines = Integer.parseInt(param.toString());
                if (lines <= 1000) { // No DoS, please...
                    tail(logFile, response.getWriter(), lines);
                } else {
                    response.getWriter().print("");
                }
            } else {
                writeAll(logFile, response.getWriter());
            }
        } else {
            response.sendRedirect("/login");
        }
    }
}
