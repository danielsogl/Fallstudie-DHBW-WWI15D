package Projekt;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Ein Personen Objekt beinhaltet alle personenbezogene Daten.
 * 
 * @author Daniel Sogl
 * @version 1.2
 */

public class Aufwand {

	private StringProperty name;
	private StringProperty zugehoerigkeit;
	private DoubleProperty pt;
	private DoubleProperty anwesenheit;

	/**
	 * Standard Konstruktor
	 * 
	 * @param name Der Name des Auwandes
	 */
	public Aufwand(String name) {
		this.setName(name);
		this.setPt(0);
	}

	/**
	 * Erweiterter Konstruktor welcher von der Datenbank-Schnittstelle aufgerufen wird
	 * 
	 * @param name Name des Aufwandes
	 * @param zugehoerigkeit Zugehörigkeit des Aufwandes
	 * @param pt PT des Aufwandes
	 */
	public Aufwand(String name, String zugehoerigkeit, double pt) {
		this.setName(name);
		this.setZugehoerigkeit(zugehoerigkeit);
		this.setPt(pt);
	}

	/**
	 * 
	 * @return String
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * 
	 * @return StringProperty
	 */
	public StringProperty nameProperty() {
		return name;
	}

	/**
	 * 
	 * @param rolle Rolle des Aufwands
	 */
	public void setName(String rolle) {
		this.name = new SimpleStringProperty(rolle);
	}

	/**
	 *  
	 * @return String
	 */
	public String getZugehoerigkeit() {
		return zugehoerigkeit.get();
	}

	/**
	 * 
	 * @return StringProperty
	 */
	public StringProperty zugehoerigkeitProperty() {
		return zugehoerigkeit;
	}

	/**
	 * 
	 * @param zugehoerigkeit des Aufwands
	 */
	public void setZugehoerigkeit(String zugehoerigkeit) {
		this.zugehoerigkeit = new SimpleStringProperty(zugehoerigkeit);
	}

	/**
	 * 
	 * @return double
	 */
	public double getPt() {
		return pt.get();
	}

	/**
	 * 
	 * @return DoubleProperty
	 */
	public DoubleProperty ptProperty() {
		return pt;
	}

	/**
	 * 
	 * @param pt PT des Auwandes
	 */
	public void setPt(double pt) {
		this.pt = new SimpleDoubleProperty(pt);
	}
	/**
	 * 
	 * @return Double
	 */
	public double getAnwesenheit() {
		return anwesenheit.get();
	}

	/**
	 * 
	 * @return DoubleProperty
	 */
	public DoubleProperty getAnwesenheitProperty() {
		return anwesenheit;
	}

	/**
	 * 
	 * @param anwesenheit Anwesenheit des Auwandes
	 */
	public void setAnwesenheit(Double anwesenheit) {
		this.anwesenheit = new SimpleDoubleProperty(anwesenheit);
	}
}
