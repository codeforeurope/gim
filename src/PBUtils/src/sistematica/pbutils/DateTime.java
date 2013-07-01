package sistematica.pbutils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Contiene funzioni di utilità per la gestione del tempo.
 */
public class DateTime {

    private DateTime() {
        // Questa classe non s'ha da istanziare...
    }

    /**
     * Restituisce una rappresentazione leggibile di un intervallo di tempo espresso in millisecondi.
     * 
     * @param msecs il tempo in millisecondi
     * @return una rappresentazione leggibile dell'intervallo
     */
    public static String msecsToStr(long msecs) {
        if (msecs < 1000) {
            return String.format("%d msecs", msecs);
        } else if (msecs < 60000) {
            return String.format("%.2f secs", msecs / 1000d);
        } else if (msecs < 3600000) {
            long secs = msecs / 1000;
            return String.format("%d mins, %d secs", secs / 60, secs % 60);
        } else {
            long secs = msecs / 1000;
            long hours = secs / 3600;
            secs = secs % 3600;
            long mins = secs / 60;
            secs = secs % 60;
            return String.format("%d hours, %d mins, %d secs", hours, mins, secs);
        }
    }

    /**
     * Restituisce il prossimo timestamp, a partire da from, per il quale i
     * minuti saranno divisibili per minutes.
     * 
     * <p>Ad esempio se from è 06-Sep-2011 12:15:00 e minutes è 10, restituirà
     * 06-Sep-2011 12:20:00.
     * 
     * @param from il timestamp di partenza
     * @param minutes la quantità per la quale dovranno essere divisibili i minuti del prossimo timestamp (da 0 a 59)
     * @return il più piccolo timestamp a partire da from avente i minuti divisibili per minutes (i secondi e i millisecondi sono azzerati)
     */
    public static Date nextDateWithMinutesDivisibleBy(Date from, int minutes) {
        if (minutes >= 60) {
            minutes %= 60;
        }

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(from);
        int currentMinutes = calendar.get(Calendar.MINUTE);
        int futureMinutes;
        int additionalHours = 0;
        if (currentMinutes % minutes == 0) {
            futureMinutes = currentMinutes;
        } else {
            futureMinutes = currentMinutes + minutes - (currentMinutes % minutes);
            if (futureMinutes >= 60) {
                additionalHours = 1;
                futureMinutes = 0;
            }
        }
        calendar.add(Calendar.HOUR, additionalHours);
        calendar.set(Calendar.MINUTE, futureMinutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Imposta ad UTC la {@link TimeZone} predefinita.
     */
    public static void setUTCTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
