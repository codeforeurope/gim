/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sistematica.sinottici;

import java.util.List;

/**
 *
 * @author Manuel
 */
public class DwrCamera {

    public List<Camera> getListCamera() {
        Sinottico sin = new Sinottico();
        return sin.getListCameras();
    }
}
