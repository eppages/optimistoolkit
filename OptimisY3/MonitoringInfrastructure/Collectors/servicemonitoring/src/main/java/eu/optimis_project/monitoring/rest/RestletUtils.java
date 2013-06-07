/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring.rest;


/**
 * Utility methods for Restlet communication
 * 

 * @author Daniel Espling <espling@cs.umu.se>
 * 
 */
public class RestletUtils {

    /*
     * 
     * 
     * private static Logger log = Logger.getLogger(RestletUtils.class); private
     * static XStream xstream = new XStream(new DomDriver());
     * 
     * /* Configure the xstream serializer
     * 
     * static { xstream.alias("measurement", Measurement.class);
     * xstream.alias("items", Items.class);
     * xstream.addImplicitCollection(Items.class, "measurements"); }
     * 
     * // Create instance of DocumentBuilderFactory private final static
     * DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
     * 
     * /** Converts a measaurement to a (DOM-based) Restlet Representation
     * 
     * @param measurement The measurement to convert
     * 
     * @return A representation
     * 
     * public static Representation measurementToRepresentation( Measurement
     * measurement) { Set<Measurement> uniSet = new HashSet<Measurement>();
     * uniSet.add(measurement); return measurementsToRepresentation(uniSet); }
     * 
     * /** Converts a set of measurements to a common Restlet representation
     * 
     * @param measurements The set of measurements to convert
     * 
     * @return A representation of the whole set of measurements
     * 
     * public static Representation measurementsToRepresentation(
     * Set<Measurement> measurements) {
     * 
     * // Get the DocumentBuilder DocumentBuilder parser; try { parser =
     * factory.newDocumentBuilder(); } catch (ParserConfigurationException e) {
     * log.fatal("Parser configuration failed, Fatal."); throw new
     * RuntimeException(e); }
     * 
     * // Create blank DOM Document Document doc = parser.newDocument();
     * 
     * // Add root object Element eRoot = doc.createElement("items");
     * doc.appendChild(eRoot);
     * 
     * // Add measurements for (Measurement m : measurements) {
     * xstream.marshal(m, new DomWriter(eRoot)); }
     * 
     * // Create Restlet DOM representation DomRepresentation domRep; try {
     * domRep = new DomRepresentation(MediaType.TEXT_XML); } catch (IOException
     * e) { log.fatal("Failed to initiate XML parser, Fatal."); throw new
     * RuntimeException(e); } domRep.setDocument(doc); return domRep; }
     * 
     * /** Converts a (DOM based) Restlet representation to a measurement object
     * 
     * @param dataRep The prepresentation
     * 
     * @param serviceID The service ID associated to the representation
     * 
     * @return A fully populated measurement object
     * 
     * public static Measurement representationToMeasurement( Representation
     * dataRep, String serviceID) {
     * 
     * if (serviceID == null) { throw new
     * IllegalArgumentException("Service ID is null"); }
     * 
     * if (dataRep == null) { throw new
     * IllegalArgumentException("Representation is null"); }
     * 
     * if (!(dataRep instanceof DomRepresentation)) { throw new
     * IllegalArgumentException( "Representation not in XML format"); }
     * 
     * Document doc = null;
     * 
     * try { doc = ((DomRepresentation) dataRep).getDocument(); } catch
     * (IOException e) { log.warn("Failed to read DomRep document"); throw new
     * IllegalStateException(e); }
     * 
     * Items items = (Items) xstream.unmarshal(new DomReader(doc));
     * System.out.println(items); List<Measurement> measurements =
     * items.getMeasurements();
     * 
     * if (measurements.size() > 1) {
     * log.warn("Found more than one measurement in set, invalid for input.");
     * // TODO Simply allow this? throw new IllegalArgumentException(
     * "Only one measurement in set is allowed for input."); }
     * 
     * Measurement m1 = measurements.get(0);
     * 
     * // Ensure match between expected and found service ID if
     * (!serviceID.equals(m1.getServiceID())) { throw new
     * IllegalArgumentException(
     * "Service ID mismatch between parsed measurement and expected service ID. Found: '"
     * + m1.getServiceID() + ", expected: " + serviceID); }
     * 
     * return m1; }
     */
}
