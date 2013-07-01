package sistematica.pbutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Questa classe astratta è progettata per contenere la configurazione del
 * programma. Le sue sottoclassi dovranno definire tanti membri dato pubblici
 * quanti sono i valori delle properties che costituiscono la configurazione.
 * 
 * <p>La corrispondenza tra i nomi delle properties, caricabili anche da file, 
 * e quelli dei membri dato, segue questa regola:
 * 
 * <pre>
 *     my.example.property = MY_EXAMPLE_PROPERTY
 * </pre>
 */
public abstract class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class);
    private static final String[] DANGEROUS_WORDS = {"password", "passwd"};

    /**
     * Controlla se il nome specificato contiene una parola indicante un dato
     * sensibile (es. password).
     * 
     * @param name il nome da controllare
     * @return true se il nome contiene una parola indicante un dato sensibile,
     * false altrimenti
     */
    private boolean containsDangerousWord(String name) {
        final String uname = name.toUpperCase();
        for (String word : DANGEROUS_WORDS) {
            if (uname.contains(word.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converte un nome di una property (es. mail.smtp) nel
     * nome del corrispondente campo della classe nel classico
     * stile delle costanti Java (es. MAIL_SMTP).
     * 
     * @param property il nome della property
     * @return il nome del corrispondente campo della classe
     */
    private String propertyNameToClassField(String property) {
        return property.toUpperCase().replace(".", "_");
    }

    /**
     * Imposta il valore di un campo della classe; funziona con
     * tutti i tipi primitivi (per i Boolean yes e' equivalente
     * true).
     * 
     * @param field l'oggetto che rappresenta il campo della classe
     * @param value il valore da impostare
     */
    private void setField(Field field, Object value) {
        try {
            String strVal = value.toString();
            if (field.getType() == String.class) {
                field.set(this, strVal);
            } else if (field.getType() == Byte.class) {
                field.set(this, Byte.parseByte(strVal));
            } else if (field.getType() == Short.class) {
                field.set(this, Short.parseShort(strVal));
            } else if (field.getType() == Integer.class) {
                field.set(this, Integer.parseInt(strVal));
            } else if (field.getType() == Long.class) {
                field.set(this, Long.parseLong(strVal));
            } else if (field.getType() == Float.class) {
                field.set(this, Float.parseFloat(strVal));
            } else if (field.getType() == Double.class) {
                field.set(this, Double.parseDouble(strVal));
            } else if (field.getType() == Boolean.class) {
                if (strVal.equalsIgnoreCase("yes")) // Non si sa mai...
                {
                    strVal = "true";
                }
                Boolean truth = Boolean.parseBoolean(strVal);
                field.set(this, truth);
            } else if (field.getType() == Character.TYPE) {
                field.set(this, strVal.charAt(0));
            } else {
                field.set(this, value); // Alla fine proviamo con qualcosa di generico
            }

            // Nascondiamo i dati sensibili!
            if (containsDangerousWord(field.getName())) {
                logger.info("\t###  " + field.getName() + " = *****");
            } else {
                logger.info("\t###  " + field.getName() + " = " + strVal);
            }

        } catch (IllegalAccessException ex) {
            logger.error("Illegal access to field " + this.getClass().getName() + "." + field.getName(), ex);
        } catch (Exception ex) {
            logger.warn("Error while reading the property for the field " + field.getName() + ", using default value", ex);
        }
    }

    /**
     * Imposta i valori dei campi di questa classe coi corrispondenti
     * valori delle properties. I nomi delle property usano la classica
     * forma qualcosa.qualcosaltro mentre i campi della classe usano
     * lo stile delle costanti Java QUALCOSA_QUALCOSALTRO.
     * 
     * @param p le properties da leggere
     */
    private void fill(Properties p) {
        Class<? extends Configuration> cls = this.getClass();

        logger.info("\t################################################");
        logger.info("\t###              CONFIGURATION");
        logger.info("\t################################################");

        // Lista delle chiavi, ordinate alfabeticamente (visto che l'ordine
        // in cui sono scritte nel file di properties è irrecuperabile).
        List<Object> keys = new ArrayList<Object>(p.keySet());
        Collections.sort(keys, new Comparator<Object>() {

            @Override
            public int compare(Object a, Object b) {
                return a.toString().compareTo(b.toString());
            }
        });

        for (Object key : keys) {
            Object value = p.getProperty(key.toString());

            if (key != null && value != null && !key.toString().startsWith("log4j.")) {
                String propertyName = key.toString();
                String fieldName = propertyNameToClassField(propertyName);

                try {
                    Field field = cls.getField(fieldName);
                    setField(field, value);
                } catch (SecurityException ex) {
                    logger.error("Security error while accessing " + cls.getName() + "." + fieldName, ex);
                } catch (NoSuchFieldException ex) {
                    logger.error(cls.getName() + "." + fieldName + " field doesn't exist", ex);
                }
            }
        }

        logger.info("\t################################################");
    }

    /**
     * Imposta i parametri di configurazione leggendoli dal file di properties
     * specificato.
     * 
     * @param f il file di properties
     */
    public void load(File f) {
        try {
            Properties p = new Properties();
            p.load(new FileReader(f));
            fill(p);
        } catch (FileNotFoundException ex) {
            logger.error("Propery file " + f + " not found.", ex);
        } catch (IOException ex) {
            logger.error("Error while reading property file " + f, ex);
        }
    }

    /**
     * Imposta i parametri di configurazione leggendoli dalle properties
     * passate come argomento.
     * 
     * @param p le properties
     */
    public void load(Properties p) {
        fill(p);
    }
}
