package gpsdatareceiver;

import java.io.File;
import java.util.Vector;

import org.jdom.Document;

public class TestApp
{
    public static void main(String[] args) throws Exception
    {
        File f = new File("G:/CVS_home/cvsroot/SISTEMATICA/PROGETTI/MOBIWORK/Mobile/GpsDataReceiver/1185705524654.blob");
        String [] outID = new String[1];
        Vector data = new Vector();
        GpsRxServlet.parseData(f, data, outID);

        Document xmlDoc = null;
        xmlDoc = GpsRxServlet.createXmlDoc(data, outID[0], System.currentTimeMillis());

        org.jdom.output.XMLOutputter xmlOut = new org.jdom.output.XMLOutputter();
        xmlOut.output(xmlDoc, System.out);
    }
}
