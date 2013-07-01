package sistematica.sinottici;

import java.util.List;

/**
 *
 * @author Manuel
 */
public class DwrSinottici {

    public List<Camera> getListCamera() {
        Sinottico sin = new Sinottico();
        return sin.getListCameras();
    }

    public List<Event> getListEvents() {
        Sinottico sin = new Sinottico();
        return sin.getListEvents();
    }
}
