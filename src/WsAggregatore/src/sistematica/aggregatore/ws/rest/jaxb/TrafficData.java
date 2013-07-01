//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.22 at 12:44:28 PM CEST 
//


package sistematica.aggregatore.ws.rest.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="location_reference" type="{http://www.5t.torino.it/simone/ns/traffic_data}location_referenceType"/>
 *         &lt;choice>
 *           &lt;element name="RD_data" type="{http://www.5t.torino.it/simone/ns/traffic_data}RD_dataType" maxOccurs="unbounded"/>
 *           &lt;element name="MRD_data" type="{http://www.5t.torino.it/simone/ns/traffic_data}MRD_dataType" maxOccurs="unbounded"/>
 *           &lt;element name="TT_data" type="{http://www.5t.torino.it/simone/ns/traffic_data}TT_type" maxOccurs="unbounded"/>
 *           &lt;element name="TT_profiled_data" type="{http://www.5t.torino.it/simone/ns/traffic_data}TTprofiledType" maxOccurs="unbounded"/>
 *           &lt;element name="OD_data" type="{http://www.5t.torino.it/simone/ns/traffic_data}OD_type" maxOccurs="unbounded"/>
 *           &lt;element name="OD_profiled_data" type="{http://www.5t.torino.it/simone/ns/traffic_data}ODprofiledType" maxOccurs="unbounded"/>
 *           &lt;element name="FDT_data" maxOccurs="unbounded">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;extension base="{http://www.5t.torino.it/simone/ns/traffic_data}FDT_type">
 *                 &lt;/extension>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="PK_data" type="{http://www.5t.torino.it/simone/ns/traffic_data}PK_type" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="datatype" use="required" type="{http://www.5t.torino.it/simone/ns/traffic_data}trafficDataType" />
 *       &lt;attribute name="generation_time" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="start_time" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="end_time" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="source" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "locationReference",
    "rdData",
    "mrdData",
    "ttData",
    "ttProfiledData",
    "odData",
    "odProfiledData",
    "fdtData",
    "pkData"
})
@XmlRootElement(name = "traffic_data")
public class TrafficData {

    @XmlElement(name = "location_reference", required = true)
    protected LocationReferenceType locationReference;
    @XmlElement(name = "RD_data")
    protected List<RDDataType> rdData;
    @XmlElement(name = "MRD_data")
    protected List<MRDDataType> mrdData;
    @XmlElement(name = "TT_data")
    protected List<TTType> ttData;
    @XmlElement(name = "TT_profiled_data")
    protected List<TTprofiledType> ttProfiledData;
    @XmlElement(name = "OD_data")
    protected List<ODType> odData;
    @XmlElement(name = "OD_profiled_data")
    protected List<ODprofiledType> odProfiledData;
    @XmlElement(name = "FDT_data")
    protected List<TrafficData.FDTData> fdtData;
    @XmlElement(name = "PK_data")
    protected List<PKType> pkData;
    @XmlAttribute(required = true)
    protected TrafficDataType datatype;
    @XmlAttribute(name = "generation_time", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar generationTime;
    @XmlAttribute(name = "start_time", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startTime;
    @XmlAttribute(name = "end_time", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endTime;
    @XmlAttribute(required = true)
    protected String source;

    /**
     * Gets the value of the locationReference property.
     * 
     * @return
     *     possible object is
     *     {@link LocationReferenceType }
     *     
     */
    public LocationReferenceType getLocationReference() {
        return locationReference;
    }

    /**
     * Sets the value of the locationReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationReferenceType }
     *     
     */
    public void setLocationReference(LocationReferenceType value) {
        this.locationReference = value;
    }

    /**
     * Gets the value of the rdData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rdData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRDData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RDDataType }
     * 
     * 
     */
    public List<RDDataType> getRDData() {
        if (rdData == null) {
            rdData = new ArrayList<RDDataType>();
        }
        return this.rdData;
    }

    /**
     * Gets the value of the mrdData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mrdData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMRDData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MRDDataType }
     * 
     * 
     */
    public List<MRDDataType> getMRDData() {
        if (mrdData == null) {
            mrdData = new ArrayList<MRDDataType>();
        }
        return this.mrdData;
    }

    /**
     * Gets the value of the ttData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ttData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTTData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TTType }
     * 
     * 
     */
    public List<TTType> getTTData() {
        if (ttData == null) {
            ttData = new ArrayList<TTType>();
        }
        return this.ttData;
    }

    /**
     * Gets the value of the ttProfiledData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ttProfiledData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTTProfiledData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TTprofiledType }
     * 
     * 
     */
    public List<TTprofiledType> getTTProfiledData() {
        if (ttProfiledData == null) {
            ttProfiledData = new ArrayList<TTprofiledType>();
        }
        return this.ttProfiledData;
    }

    /**
     * Gets the value of the odData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the odData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getODData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ODType }
     * 
     * 
     */
    public List<ODType> getODData() {
        if (odData == null) {
            odData = new ArrayList<ODType>();
        }
        return this.odData;
    }

    /**
     * Gets the value of the odProfiledData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the odProfiledData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getODProfiledData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ODprofiledType }
     * 
     * 
     */
    public List<ODprofiledType> getODProfiledData() {
        if (odProfiledData == null) {
            odProfiledData = new ArrayList<ODprofiledType>();
        }
        return this.odProfiledData;
    }

    /**
     * Gets the value of the fdtData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fdtData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFDTData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrafficData.FDTData }
     * 
     * 
     */
    public List<TrafficData.FDTData> getFDTData() {
        if (fdtData == null) {
            fdtData = new ArrayList<TrafficData.FDTData>();
        }
        return this.fdtData;
    }

    /**
     * Gets the value of the pkData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pkData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPKData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PKType }
     * 
     * 
     */
    public List<PKType> getPKData() {
        if (pkData == null) {
            pkData = new ArrayList<PKType>();
        }
        return this.pkData;
    }

    /**
     * Gets the value of the datatype property.
     * 
     * @return
     *     possible object is
     *     {@link TrafficDataType }
     *     
     */
    public TrafficDataType getDatatype() {
        return datatype;
    }

    /**
     * Sets the value of the datatype property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrafficDataType }
     *     
     */
    public void setDatatype(TrafficDataType value) {
        this.datatype = value;
    }

    /**
     * Gets the value of the generationTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGenerationTime() {
        return generationTime;
    }

    /**
     * Sets the value of the generationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGenerationTime(XMLGregorianCalendar value) {
        this.generationTime = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartTime(XMLGregorianCalendar value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndTime(XMLGregorianCalendar value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.5t.torino.it/simone/ns/traffic_data}FDT_type">
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class FDTData
        extends FDTType
    {


    }

}
