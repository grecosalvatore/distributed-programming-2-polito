//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.12.28 at 02:13:48 PM CET 
//


package it.rnsinfo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumVehicleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumVehicleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CAR"/>
 *     &lt;enumeration value="TRUCK"/>
 *     &lt;enumeration value="CARAVAN"/>
 *     &lt;enumeration value="SHUTTLE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "enumVehicleType")
@XmlEnum
public enum EnumVehicleType {

    CAR,
    TRUCK,
    CARAVAN,
    SHUTTLE;

    public String value() {
        return name();
    }

    public static EnumVehicleType fromValue(String v) {
        return valueOf(v);
    }

}
