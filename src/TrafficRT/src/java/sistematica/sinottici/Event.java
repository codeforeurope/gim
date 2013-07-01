package sistematica.sinottici;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private static final Map<String, String> EVENT_TYPE_NAMES = new HashMap<String, String>();
    private static final String EVENT_DEFAULT_NAME = "Evento generico";
    
    private static final Map<String, String> EVENT_TYPE_ICONS = new HashMap<String, String>();
    private static final String EVENT_DEFAULT_ICON = "images/default_event.png";

    static {
        EVENT_TYPE_NAMES.put("road_work", "Lavori stradali");
        EVENT_TYPE_NAMES.put("car_crash", "Incidente");
        
        EVENT_TYPE_ICONS.put("road_work", "images/road_work.png");
        EVENT_TYPE_ICONS.put("car_crash", "images/car_crash.png");
    }
    private String urlIco;
    private double latitude;
    private double longitude;
    private String html;

    public Event(String type, String streetName, double latitude, double longitude) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table><tr><td style='background:#3399FF; color:#FFFFFF'>Evento</td><td>");
        sb.append(EVENT_TYPE_NAMES.containsKey(type) ? EVENT_TYPE_NAMES.get(type) : EVENT_DEFAULT_NAME);
        if (streetName != null) {
            sb.append("</td></tr><tr><td style='background:#3399FF; color:#FFFFFF'>Luogo</td><td>");
            sb.append(streetName);
        }
        sb.append("</td></tr><tr><td style='background:#3399FF; color:#FFFFFF'>Latitudine</td><td>");
        sb.append(String.format("%.6f", latitude));
        sb.append("</td></tr><tr><td style='background:#3399FF; color:#FFFFFF'>Longitudine</td><td>");
        sb.append(String.format("%.6f", longitude));
        sb.append("</td></tr></table>");
        html = sb.toString();
        
        this.latitude = latitude;
        this.longitude = longitude;
        this.urlIco = EVENT_TYPE_ICONS.containsKey(type) ? EVENT_TYPE_ICONS.get(type) : EVENT_DEFAULT_ICON;
    }

    public String getUrlIco() {
        return urlIco;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getHtml() {
        return html;
    }
}
