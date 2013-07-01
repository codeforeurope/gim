/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sistematica.sinottici;

import java.sql.Connection;
import java.util.List;
import sistematica.webcontext.DataSources;
import sistematica.webcontext.Settings;

/**
 *
 * @author Manuel
 */
public class Sinottico {

    public List<Street> getAllStreet() {
        Connection conn = null;
        List<Street> listStr = null;
        try {
            conn = DataSources.getConnection(Settings.DATA_SOURCE_NAME);

            SinotticoDAO sinDAO = new SinotticoDAO();
            listStr = sinDAO.getAllStreet(conn);

        } finally {
            DataSources.returnConnection(conn);
            return listStr;
        }
    }

    public List<Camera> getListCameras() {
        Connection conn = null;
        List<Camera> listCam = null;
        try {
            conn = DataSources.getConnection(Settings.DATA_SOURCE_NAME);

            SinotticoDAO sinDAO = new SinotticoDAO();
            listCam = sinDAO.getListCameras(conn);
        } finally {
            DataSources.returnConnection(conn);
            return listCam;
        }
    }

    public List<Event> getListEvents() {
        Connection conn = null;
        List<Event> listEv = null;
        try {
            conn = DataSources.getConnection(Settings.SISTEMA_DATA_SOURCE_NAME);
            
            SinotticoDAO sinDAO = new SinotticoDAO();
            listEv = sinDAO.getListEvents(conn);
        } finally {
            DataSources.returnConnection(conn);
            return listEv;
        }
    }
}
