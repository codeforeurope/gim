package sistematica.gim.datex;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.zip.GZIPOutputStream;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

@Path("/")
public class Resource {

    public static final DateFormat DATEX_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static final Logger logger = Logger.getLogger(Resource.class);
    private Random random = new Random();
    private MeasurementsDAO dao;
    private VelocityEngine velocity;
    private Template template;

    private void initDAO() throws NamingException {
        if (dao == null) {
            dao = new MeasurementsDAO();
        }
    }

    private void initVelocity() {
        if (velocity == null) {
            velocity = new VelocityEngine();
            Properties p = new Properties();
            p.setProperty("resource.loader", "classpath");
            p.setProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
            velocity.init(p);
        }

        if (template == null) {
            template = velocity.getTemplate("/sistematica/gim/datex/tpl/datex_traffic.xml");
        }
    }

    private File getTempFile() {
        File tmpDir = new File(Configuration.getInstance().TMP_DIR);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        
        String prefix = new File(tmpDir, FILE_DATE_FORMAT.format(new Date()) + "_datex_").getAbsolutePath();
        File temp;
        do {
            temp = new File(prefix + random.nextInt(Integer.MAX_VALUE) + ".xml.gz");
        } while (temp.exists());
        return temp;
    }

    @GET
    @Path("traffic")
    @Produces({"application/gzip"})
    public byte[] datex(
            @Context HttpServletResponse response,
            @DefaultValue("0") @QueryParam("from") long from,
            @DefaultValue("" + Long.MAX_VALUE) @QueryParam("to") long to) {

        try {
            initDAO();
            initVelocity();

            // Chooses a random temporary file name
            File file = getTempFile();
            logger.debug(String.format("Generating DATEX output (from %d to %d): %s", from, to, file));

            // Reads the data from the DB and merges it with Velocity's template
            long start = System.currentTimeMillis();
            List<Measurement> measurements = dao.getMeasurements(from, to);
            logger.debug(String.format("Data read from the DB in %d seconds (%s)", (System.currentTimeMillis() - start) / 1000, file));
            VelocityContext context = new VelocityContext();
            context.put("publicationTime", DATEX_DATE_FORMAT.format(new Date()));
            context.put("measurements", measurements);

            // Writes the DATEX to the temporary file
            start = System.currentTimeMillis();
            FileOutputStream out = new FileOutputStream(file);
            BufferedOutputStream buffered = new BufferedOutputStream(out);
            GZIPOutputStream gzip = new GZIPOutputStream(buffered);
            OutputStreamWriter writer = new OutputStreamWriter(gzip);
            template.merge(context, writer);
            writer.close();
            logger.debug(String.format("Temporary output written in %d seconds (%s)", (System.currentTimeMillis() - start) / 1000, file));

            // Reads the temporary file and returns its content
            start = System.currentTimeMillis();
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
            byte[] data = new byte[(int) file.length()];
            input.read(data);
            input.close();
            logger.debug(String.format("Temporary data read in %d seconds (%s)", (System.currentTimeMillis() - start) / 1000, file));
            response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
            return data;
        } catch (Exception ex) {
            logger.error("Error while generating DATEX", ex);
            return new byte[]{};
        }
    }
}
