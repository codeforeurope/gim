//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.22 at 12:44:28 PM CEST 
//


package sistematica.aggregatore.ws.rest.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for trafficDataType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="trafficDataType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="misura"/>
 *     &lt;enumeration value="previsione"/>
 *     &lt;enumeration value="profilo"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "trafficDataType")
@XmlEnum
public enum TrafficDataType {

    @XmlEnumValue("misura")
    MISURA("misura"),
    @XmlEnumValue("previsione")
    PREVISIONE("previsione"),
    @XmlEnumValue("profilo")
    PROFILO("profilo");
    private final String value;

    TrafficDataType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TrafficDataType fromValue(String v) {
        for (TrafficDataType c: TrafficDataType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
