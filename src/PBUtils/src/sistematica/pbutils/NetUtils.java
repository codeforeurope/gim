package sistematica.pbutils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Funzioni di utilità per la programmazione di rete.
 */
public class NetUtils {

    /**
     * Controlla se una porta è disponibile.
     *
     * @param port la porta di cui bisogna controllare la disponibilità
     * @return true se la porta è disponibile, false altrimenti
     */
    public static boolean isPortAvailable(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }

        return false;
    }
}
