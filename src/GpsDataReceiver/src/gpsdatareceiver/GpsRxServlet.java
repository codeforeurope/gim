package gpsdatareceiver;

import gpsdatareceiver.routines.Routines;
import gpsdatareceiver.webcontext.StartApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsistematica.text.CString;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class GpsRxServlet extends HttpServlet
{
    private static final int MD5_ASCII_ENCODE_LENGTH = 32;

    static org.apache.log4j.Logger m_log4j = org.apache.log4j.Logger.getLogger(GpsRxServlet.class);
    static java.text.SimpleDateFormat m_sdfInput = new java.text.SimpleDateFormat("yyMMddHHmmss");
    static java.text.SimpleDateFormat m_sdfOutput = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String PARAM_TMP_DIR = "gps.data.receiver.blob.tmp.dir"; //"tmp-folder";
    private static final String PARAM_CPY_DIR = "gps.data.receiver.blob.copy.dir"; //"copy-path";
    private static final String PARAM_WORK_DIR = "gps.data.receiver.xml.input.dir"; //"out-folder";
    
    private static final String PARAM_GOOD_FILE_DIR = "gps.data.receiver.xml.elaborated.good.dir";
    private static final String PARAM_BAD_FILE_DIR = "gps.data.receiver.xml.elaborated.bad.dir";
    
    private static final String PARAM_NEWLINE = "newline";
    private static final String PARAM_ENC = "encoding";

    String m_tmpDir;
    String m_cpyDir;
    String m_workDir;
    
    String m_goodFileDir;
    String m_badFileDir;

//    private final String CONTENT_TYPE = "text/plain";
    private final String CONTENT_TYPE = "application/x-gzip";

    private XMLOutputter m_xout = new XMLOutputter();

    //Initialize global variables
    public void init(ServletConfig config) throws ServletException
    {
        m_log4j.debug("INIT APPLICATION");

        //ServletContext ctx = config.getServletContext();
        m_tmpDir = StartApp.getProperty(PARAM_TMP_DIR); //config.getInitParameter(PARAM_TMP_DIR);
        m_workDir = StartApp.getProperty(PARAM_WORK_DIR); //config.getInitParameter(PARAM_WORK_DIR);
        m_cpyDir = StartApp.getProperty(PARAM_CPY_DIR); //config.getInitParameter(PARAM_CPY_DIR);
        
        m_goodFileDir = StartApp.getProperty(PARAM_GOOD_FILE_DIR);
        m_badFileDir = StartApp.getProperty(PARAM_BAD_FILE_DIR);
    }

    //Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        m_log4j.info("POST from " + request.getRemoteAddr());
        
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();

        long now;
        //si evita che a causa della concorrenza venga assegnato lo stesso nome a pi� file
        synchronized (this)
        {
            now = System.currentTimeMillis();
        }

        //-------------------------------
        // salva su file dati ricevuti
        //-------------------------------
        File rxDataFile = saveDataReceived(request, now);
        
        m_log4j.info("FILE SALAVATO IN " + rxDataFile.getAbsolutePath());

        //-------------------------------
        // parsa i dati ricevuti
        //-------------------------------
        Vector gpsDataList = new Vector();
        String [] idDevice = new String[1];
        String result = parseData(rxDataFile, gpsDataList, idDevice);
        if (!result.equalsIgnoreCase("OK"))
        {
            m_log4j.info("Result: " + result);
            out.write(result);
            out.flush();
            return;
        }

        //-------------------------------
        //crea il documento su XML
        //-------------------------------
        Document xmlDoc = null;
        xmlDoc = createXmlDoc(gpsDataList, idDevice[0], now);

        //
        //scrive su file XML
        //
        String xmlFileName = now + ".xml";

        File tmpFile = new File(m_tmpDir, xmlFileName);

        xmlToFile(tmpFile, xmlDoc);

        File archiveFile = new File(m_cpyDir, xmlFileName);
        copy(tmpFile, archiveFile);

        File destFile = new File(m_workDir, xmlFileName);
        move(tmpFile, destFile);

        m_log4j.info("Esecuzione terminata correttamente - created file " + xmlFileName);
        
        //-------------------------------
        //inserisci i dati nel db
        //-------------------------------
        gpsdatareceiver.routines.Routines routines = new Routines(m_workDir, m_badFileDir, m_goodFileDir);
		try
		{
			routines.prepare();
			
			if(destFile != null && destFile.isFile())
			{
				routines.analyze(destFile);
			}
		}
		catch(Exception e)
		{
			m_log4j.error(e.getMessage(), e);
		}

        result = "OK";
        m_log4j.info("Result: " + result);
        out.write(result);
        out.flush();
        return;
    }

    /**
     * salva su file locale i dati binari ricevuti nel body della POSt HTTP
     *
     * @param request HttpServletRequest
     * @param now long
     * @return File
     * @throws IOException
     */
    private File saveDataReceived(HttpServletRequest request, long now) throws IOException
    {
        InputStream is = request.getInputStream();
        
        // DECOMPRESS DATA
        GZIPInputStream gzipInputStream = null;
		gzipInputStream = new GZIPInputStream(is);
		
		m_log4j.debug("Zipped data length " + is.available() + " bytes");
        
        FileOutputStream fos = null;
        int len = 0;
        byte[] buffer = null;
        File rxDataFile = null;
        try
        {
            rxDataFile = new File(m_cpyDir, now + ".blob");
            fos = new FileOutputStream(rxDataFile);
            len = 0;
            buffer = new byte[100];
            while ( (len = gzipInputStream.read(buffer, 0, 100)) > 0) // is
            {
                fos.write(buffer, 0, len);
            }
        }
        catch (Throwable th)
        {
            m_log4j.error("Error saving input data to file", th);
        }
        finally
        {
            is.close();
            fos.close();
        }
        return rxDataFile;
    }

    // Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file) throws IOException
    {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE)
        {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[ (int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
        {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length)
        {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }


    static public String parseData(File rxDataFile, Vector outGpsDataList, String outID[]) throws IOException
    {
        File tmpDataFile = null;
        String line = "", username = "";

        if (rxDataFile.length() == 0)
        {
            m_log4j.error("No data");
            return "ERR-0";
        }

        try
        {
            tmpDataFile = new File(rxDataFile.getAbsolutePath() + ".tmp");
            copy(rxDataFile, tmpDataFile);

            //
            // LETTURA INTESTAZIONE CON USERNAME
            //
            BufferedReader br = new BufferedReader(new FileReader(tmpDataFile));
            line = br.readLine();
            if (!line.startsWith("GPS-"))
            {
                m_log4j.error("Invalid header: " + line);
                return "ERR-1";
            }

            m_log4j.debug("Valid header found: " + line);

            username = line.substring(4);

            //long imei;
            try
            {
                if (username.endsWith("\n"))
                {
                    username = username.substring(0, username.length() - 2);
                }
                if (username.endsWith("\r"))
                {
                    username = username.substring(0, username.length() - 2);
                }
                username = BlankRemover.trim(username);
                //imei = Long.parseLong(tmp);
            }
            //catch (NumberFormatException ex)
            catch(Throwable th)
            {
                m_log4j.error("Invalid USERNAME: " + username, th);
                m_log4j.error("" + username.length());
                return "ERR-2";
            }

            m_log4j.debug("username: " + username);

            //outID[0] = String.valueOf(imei);
            outID[0] = username;

            br.close();

            //
            // verifica hash MD5 con password
            //
            byte [] hashMd5 = new byte[MD5_ASCII_ENCODE_LENGTH];
            byte [] allFile = getBytesFromFile(tmpDataFile);
            System.arraycopy(allFile, allFile.length-MD5_ASCII_ENCODE_LENGTH, hashMd5, 0, MD5_ASCII_ENCODE_LENGTH);
            String allFileTxt = new String(allFile, 0, allFile.length-MD5_ASCII_ENCODE_LENGTH);
            /*
            ResourceBundle rb = ResourceBundle.getBundle("users");
            String pwd;
            try
            {
                 pwd = rb.getString(username);
            }
            catch (Exception ex)
            {
                m_log4j.error("USERNAME not found: " + username, ex);
                return "ERR-2";
            }

            byte [] calculatedHash = getKeyedDigest(allFileTxt, pwd);
            String calculatedHashStr = convertByteArrayToString(calculatedHash);
            byte [] calculatedHashAsciiEnc = calculatedHashStr.getBytes();

            for (int i = 0; i<MD5_ASCII_ENCODE_LENGTH; i++)
            {
                if (hashMd5[i] != calculatedHashAsciiEnc[i])
                {
                    m_log4j.error("Md5 Hash mismatch: maybe invalid password");
                    return "ERR-2";
                }
            }

            m_log4j.debug("hash check OK");
            */
            m_log4j.debug("hash check skipped");

            //
            // PARSING DEI DATI GPS
            //
            br = new BufferedReader(new StringReader(allFileTxt));
            line = br.readLine();
            //Vector gpsDataList = new Vector();
            //while (is.readLine(buffer, 0, buffer.length) > 0)
            int i = 0;
            while ( (line = br.readLine()) != null)
            {
                i++;
                GpsData gpsData = parseGpsDataLine(line);
                m_log4j.debug("#" + i + " - " + gpsData.toString());
                if (gpsData.isValid())
                {
                    outGpsDataList.add(gpsData);
                }
            }

            m_log4j.debug("Found " + i + " data lines, " + outGpsDataList.size() + " valid gps data");
            return "OK";
        }
        finally
        {
            if (tmpDataFile != null && tmpDataFile.exists())
            {
                tmpDataFile.delete();
            }
        }
    }

    /**
     * conversione campo binario payload in stringa (ad es. visualizzabile su log o pagina web)
     *
     * @param payload byte[]
     * @return String
     */
    private static String convertByteArrayToString(byte[] payload)
    {
      String payloadStr = "";
      if (payload != null)
      {
        for (int i = 0; i < payload.length; i++)
        {
          byte b = payload[i];

          int uInt = ( (int) b) & 0xFF;
          java.math.BigInteger bi = java.math.BigInteger.valueOf(uInt);
          if (bi.longValue() < 0x10)
          {
            payloadStr += "0";
          }
          payloadStr += (bi).toString(16).toUpperCase();
        }
      }
      return payloadStr;
    }


    //Clean up resources
    public void destroy()
    {
    }

    public static GpsData parseGpsDataLine(String line)
    {
        GpsData gpsData = new GpsData();
        StringTokenizer strTok = new StringTokenizer(line, ";");

        int i = 0;
        while (strTok.hasMoreTokens())
        {
            String token = strTok.nextToken();

            switch (i)
            {
                case 0:
                    try
                    {
                        gpsData.setTimestamp(m_sdfInput.parse(token));
                    }
                    catch (ParseException ex)
                    {
                        m_log4j.error(ex);
                    }
                    break;

                case 1:
                    try
                    {
                        gpsData.setLatitude(new Double(token));
                    }
                    catch (NumberFormatException ex)
                    {
                        m_log4j.error(ex);
                    }
                    break;

                case 2:
                    try
                    {
                        gpsData.setLongitude(new Double(token));
                    }
                    catch (NumberFormatException ex)
                    {
                        m_log4j.error(ex);
                    }
                    break;
                    
                case 3:
                    try
                    {
                        gpsData.setAltitude(new Double(token));
                    }
                    catch (NumberFormatException ex)
                    {
                        m_log4j.error(ex);
                    }
                    break;

                case 4:
                    try
                    {
                        gpsData.setSpeed(new Float(token));
                    }
                    catch (NumberFormatException ex)
                    {
                        m_log4j.error(ex);
                    }
                    break;

                case 5:
                    try
                    {
                        float f = Float.parseFloat(token);
                        gpsData.setHeading(new Integer(Math.round(f)));
                    }
                    catch (NumberFormatException ex)
                    {
                        m_log4j.error(ex);
                    }
                    break;

                case 6:
                    try
                    {
                        gpsData.setSatellitesUsed(new Integer(token));
                    }
                    catch (NumberFormatException ex)
                    {
                        m_log4j.error(ex);
                    }
                    break;

                case 7:
                    try
                    {
                        gpsData.setSatellitesInView(new Integer(token));
                    }
                    catch (NumberFormatException ex)
                    {
                        m_log4j.error(ex);
                    }
                    break;
            }

            if (gpsData.getSatellitesUsed() != null &&
                gpsData.getSatellitesUsed().intValue() > 0 &&
                gpsData.getLatitude() != null &&
                gpsData.getLongitude() != null &&
                gpsData.getTimestamp() != null)
            {
                gpsData.setValid(true);
            }

            i++;
        }

        return gpsData;
    }

    /**
     * a partire dai dati di risposta forniti dalla chiamata SOAP costruisce un
     * documento XML
     *
     * @param gpsDataList RecordMezzo[]
     * @param idDevice String
     * @param tstamp long
     * @return Document
     */
    public static Document createXmlDoc(Vector gpsDataList, String idDevice, long tstamp)
    {
        Element rootElement = new Element("stuMessages");
        Document doc = new Document(rootElement);

        String dateStr = m_sdfOutput.format(new java.util.Date(tstamp));
        rootElement.setAttribute("timestamp", dateStr);
        rootElement.setAttribute("messageID", String.valueOf(tstamp));

        for (int i = 0; i < gpsDataList.size(); i++)
        {
            GpsData gpsData = (GpsData)gpsDataList.get(i);

            Element msg = new Element("stuMessage");
            doc.getRootElement().addContent(msg);

            {
                Element ID = new Element("ID");
                String tmp = CString.isNullOrEmptyReplace(idDevice);
                ID.setText(tmp);
                msg.addContent(ID);
            }

            {
                Element ID = new Element("nome");
                String tmp = CString.isNullOrEmptyReplace(idDevice);
                ID.setText(tmp);
                msg.addContent(ID);
            }

            /*Element IDflotta = new Element("id-flotta");
            tmp = CString.isNullOrEmptyReplace(String.valueOf(recordMezzo.getID_FLOTTA()));
            IDflotta.setText(tmp);
            msg.addContent(IDflotta);*/

            /*Element nome = new Element("nome");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getNOME());
            nome.setText(tmp);
            msg.addContent(nome);*/

            /*Element data_evento = new Element("data-evento");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getDATAEVENTO());
            data_evento.setText(tmp);
            msg.addContent(data_evento);*/

            {
                Element data_posizione = new Element("data-posizione");
                String tmp = m_sdfOutput.format(gpsData.getTimestamp());
                data_posizione.setText(tmp);
                msg.addContent(data_posizione);
            }

            {
                Element lat = new Element("lat");
                String tmp = CString.isNullOrEmptyReplace(String.valueOf(gpsData.getLatitude()));
                lat.setText(tmp);
                msg.addContent(lat);
            }

            {
                Element lon = new Element("lon");
                String tmp = CString.isNullOrEmptyReplace(String.valueOf(gpsData.getLongitude()));
                lon.setText(tmp);
                msg.addContent(lon);
            }
            
            {
                Element alt = new Element("alt");
                String tmp = CString.isNullOrEmptyReplace(String.valueOf(gpsData.getAltitude()));
                alt.setText(tmp);
                msg.addContent(alt);
            }

            {
                Element vel = new Element("vel");
                String tmp = CString.isNullOrEmptyReplace(String.valueOf(gpsData.getSpeed()));
                vel.setText(tmp);
                msg.addContent(vel);
            }

            {
                Element head = new Element("heading");
                String tmp = CString.isNullOrEmptyReplace(String.valueOf(gpsData.getHeading()));
                head.setText(tmp);
                msg.addContent(head);
            }

            {
                Element numSatUsed = new Element("num-sat-used");
                String tmp = CString.isNullOrEmptyReplace(String.valueOf(gpsData.getSatellitesUsed()));
                numSatUsed.setText(tmp);
                msg.addContent(numSatUsed);
            }

            {
                Element numSatViewed = new Element("num-sat-viewed");
                String tmp = CString.isNullOrEmptyReplace(String.valueOf(gpsData.getSatellitesInView()));
                numSatViewed.setText(tmp);
                msg.addContent(numSatViewed);
            }

            /*Element codice_hw = new Element("codice-hw");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getCODICEHW());
            codice_hw.setText(tmp);
            msg.addContent(codice_hw);

            Element digitali = new Element("digitali");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getDIGITALI());
            digitali.setText(tmp);
            msg.addContent(digitali);

            Element errore = new Element("errore");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getERRORE());
            errore.setText(tmp);
            msg.addContent(errore);

            Element indirizzo = new Element("indirizzo");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getINDIRIZZO());
            indirizzo.setText(tmp);
            msg.addContent(indirizzo);

            Element citta = new Element("citta");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getCITTA());
            citta.setText(tmp);
            msg.addContent(citta);

            Element nazione = new Element("nazione");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getNAZIONE());
            nazione.setText(tmp);
            msg.addContent(nazione);

            Element id_viaggio = new Element("id-viaggio");
            tmp = CString.isNullOrEmptyReplace(recordMezzo.getIDViaggio());
            id_viaggio.setText(tmp);
            msg.addContent(id_viaggio);

            Element km = new Element("km");
            tmp = CString.isNullOrEmptyReplace(String.valueOf(recordMezzo.getKM()));
            km.setText(tmp);
            msg.addContent(km);

            Element km_percorsi = new Element("km-percorsi");
            tmp = CString.isNullOrEmptyReplace(String.valueOf(recordMezzo.getKM_PERCORSI()));
            km_percorsi.setText(tmp);
            msg.addContent(km_percorsi);

            Element km_rim = new Element("km-rimanenti");
            tmp = CString.isNullOrEmptyReplace(String.valueOf(recordMezzo.getKM_RIMANENTI()));
            km_rim.setText(tmp);
            msg.addContent(km_rim);*/
        }

        return doc;
    }

    /**
     * scrive su file un documento XML
     *
     * @param what File
     * @param xml Document
     * @throws IOException
     */
    private void xmlToFile(File what, Document xml) throws IOException
    {
        //replace existing
        if (what.exists())
        {
            what.delete();
            m_log4j.debug("Existing file removed");
        }

        what.createNewFile();
        //let other threads to execute
        //Thread.yield();
        m_log4j.debug("New file created");

        FileWriter fw = null;

        try
        {
            //writes
            fw = new FileWriter(what);

            synchronized (m_xout)
            {
                m_xout.output(xml, fw);
            }

            m_log4j.debug("Data wrote");
        }
        finally
        {
            if (fw != null)
            {
                try
                {
                    fw.close();
                }
                catch (Throwable t)
                {}

                fw = null;
            }
        }
    }

    protected static void copy(File src, File dst) throws IOException
    {
        try
        {
            // Create channel on the source
            FileChannel srcChannel = new FileInputStream(src).getChannel();

            // Create channel on the destination
            FileChannel dstChannel = new FileOutputStream(dst).getChannel();

            // Copy file contents from source to destination
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

            // Close the channels
            srcChannel.close();
            dstChannel.close();
        }
        catch (IOException e)
        {
            throw new IOException("System error - cannot copy source file, \n" +
                                  "from: " + src.getAbsolutePath() + "\n" +
                                  "to: " + dst.getAbsolutePath());
        }
    }

    /**
     * Rinomina il file sorgente come specificato dal file destinazione, se quest'ultimo � una directory il file
     * sorgente viene semplicemente spostato. Restituisce il file destinazione.
     *
     * @param fileSource File sorgente
     * @param fileDestination File destinazione
     * @return File destinazione
     * @throws IOException
     */
    protected static File move(File fileSource, File fileDestination) throws IOException
    {
        try
        {
            if (fileDestination.isDirectory())
            {
                fileDestination = new File(fileDestination.toString() + File.separator + fileSource.getName());
            }

            fileDestination.delete();

            if (!fileSource.renameTo(fileDestination))
            {
                throw new IOException("System error - cannot move source file, \n" +
                                      "from: " + fileSource.getAbsolutePath() + "\n" +
                                      "to: " + fileDestination.getAbsolutePath());
            }

            return fileDestination;
        }
        catch (IOException e)
        {
            throw e; //new IOException(e.getMessage());
        }
    }

    public static byte [] getKeyedDigest(String allFileTxt, String pwd)
    {
        byte[] md5Bytes = null;
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] inputBytes = allFileTxt.getBytes();
            byte[] keyBytes = pwd.getBytes();
            md5.update(inputBytes);
            md5.update(keyBytes);

            md5Bytes = md5.digest();

            //strMD5 = new sun.misc.BASE64Encoder().encode(md5Bytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            m_log4j.error("Problems calculating MD5", e);

            /*String[] names = getCryptoImpls("MessageDigest");
            for (int i = 0; i < names.length; i++)
            {
                m_log4j.debug(names[i]);
            }*/
        }
        return md5Bytes;
    }
}
