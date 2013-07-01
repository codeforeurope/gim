/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sistematica.sinottici;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Manuel
 */
public class SinotticoDAO extends BaseDAO {

    private static final Logger log = Logger.getLogger(SinotticoDAO.class);
    private static final String _GET_ALL_STREET = "SELECT id,name,type,lat_up,lon_up,lat_down,lon_down FROM t_street";
    private static final String _GET_CAMERAS = "SELECT id,link,name,description,latitude,longitude FROM cameras";
    private static final String _GET_EVENTS = "SELECT etyp AS event_type,asText(rpnt) AS event_position,COALESCE(strt.name, link.name) AS event_street_name FROM evnt LEFT JOIN strt ON strt.idno = evnt.strt AND (strt.dirx = evnt.sdir OR strt.dirx = 0) LEFT JOIN link ON link.idno = evnt.link AND (link.dirx = evnt.dirx OR link.dirx = 0)";

    public List<Street> getAllStreet(Connection conn) throws Exception {
        List<Street> lista = new LinkedList<Street>();

        Statement st = null;
        ResultSet rs = null;


        if (log.isDebugEnabled()) {
            log.debug("getAllStreet - SQL: " + _GET_ALL_STREET);
        }

        try {
            st = conn.createStatement();
            rs = st.executeQuery(_GET_ALL_STREET);
            int id;
            String name;
            String type;
            double latUp, lonUp, latDown, lonDown;
            while (rs.next()) {
                id = rs.getInt("ID");
                name = rs.getString("NAME");
                type = rs.getString("TYPE");
                latUp = rs.getDouble("LAT_UP");
                lonUp = rs.getDouble("LON_UP");
                latDown = rs.getDouble("LAT_DOWN");
                lonDown = rs.getDouble("LON_DOWN");
                Street str = new Street(id, name, type, latUp, lonUp, latDown, lonDown);
                lista.add(str);
            }
        } finally {
            close(rs);
            close(st);
            return lista;
        }
    }

    public List<Camera> getListCameras(Connection conn) throws Exception {

        List<Camera> listCameras = new LinkedList<Camera>();
        Statement st = null;
        ResultSet rs = null;

        if (log.isDebugEnabled()) {
            log.debug("getListCameras - SQL: " + _GET_CAMERAS);
        }

        try {
            st = conn.createStatement();
            rs = st.executeQuery(_GET_CAMERAS);
            int id;
            String link, name, description;
            double latitude, longitude;

            while (rs.next()) {
                id = rs.getInt("ID");
                link = rs.getString("LINK");
                name = rs.getString("NAME");
                description = rs.getString("DESCRIPTION");
                latitude = rs.getDouble("LATITUDE");
                longitude = rs.getDouble("LONGITUDE");

                Camera cam = new Camera(id, link, name, description, latitude, longitude);
                listCameras.add(cam);

            }

        } finally {
            close(rs);
            close(st);
            return listCameras;
        }
    }

    public List<Event> getListEvents(Connection conn) throws Exception {

        List<Event> listEvents = new LinkedList<Event>();
        Statement st = null;
        ResultSet rs = null;

        if (log.isDebugEnabled()) {
            log.debug("getListEvents - SQL: " + _GET_EVENTS);
        }

        try {
            st = conn.createStatement();
            rs = st.executeQuery(_GET_EVENTS);

            while (rs.next()) {
                String type = rs.getString("event_type");

                //POINT(12.588100431591922 41.893461780158624)
                String position = rs.getString("event_position");
                String streetName = rs.getString("event_street_name");
                System.out.println(position);
                if (position != null && position.length() > 0) {
                    //Tolgo POINT( ora la nuova stringa sarà 12.588100431591922 41.893461780158624)
                    position = position.substring(6, position.length() - 1);
                    //Tolgo ) ora la nuova stringa sarà 12.588100431591922 41.893461780158624
                    position = position.substring(0, position.length() - 1);

                    String[] array_point = position.split(" ");
                    double longitude = Double.parseDouble(array_point[0]);
                    double latitude = Double.parseDouble(array_point[1]);

                    Event ev = new Event(type, streetName, latitude, longitude);
                    listEvents.add(ev);
                }
            }

        } finally {
            close(rs);
            close(st);
            return listEvents;
        }
    }
}
