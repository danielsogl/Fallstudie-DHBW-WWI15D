package projektDaten;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Ein projektDaten-Objekt enthält neben den Projektdaten, die dazu gehörenden
 * Kompetenzen, Phasen und Beteiligten Personen.
 * 
 * @author Daniel Sogl
 *
 */
public class Projekt {
	private List<Phase> phasen = new ArrayList<Phase>();
	private List<Kompetenz> kompetenzen = new ArrayList<Kompetenz>();
	private BooleanProperty abgeschickt;
	private StringProperty name;
	private StringProperty ersteller;
	private StringProperty startDate;
	private StringProperty endDate;

	/**
	 * Konstruktor um die Grundinformationen eines Projektes abbilden zu können
	 * 
	 * @param name Name des Projektes
	 * @param ersteller Ersteller des Projektes
	 * @param abgeschickt Ob das projekt abgeschickt wurde
	 */
	public Projekt(String name, String ersteller, boolean abgeschickt) {
		this.setName(name);
		this.setErsteller(ersteller);
		this.setAbgeschickt(abgeschickt);
		this.setStartDate(null);
		this.setEndDate(null);
	}

	/**
	 * Konstruktor welcher von der Datenbankklasse aufgerufen wird
	 * @param name Name des Projektes
	 * @param ersteller Ersteller des Projektes
	 * @param abgeschickt Ob das projekt abgeschickt wurde
	 * @param startDate Startdatum des Projektes
	 * @param endDate Enddatum des Projektes
	 */
	public Projekt(String name, String ersteller, boolean abgeschickt, String startDate, String endDate) {
		this.setName(name);
		this.setErsteller(ersteller);
		this.setAbgeschickt(abgeschickt);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setPhasen(null);
		this.setKompetenzen(null);
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
	 * @param name Name des Projektes
	 */
	public void setName(String name) {
		this.name = new SimpleStringProperty(name);
	}

	/**
	 *
	 * @return ArrayList Phasen-ArrayListe
	 */
	public ArrayList<Phase> getPhasen() {
		return (ArrayList<Phase>) phasen;
	}

	/**
	 * 
	 * @param phasen Liste von Phasen
	 */
	public void setPhasen(List<Phase> phasen) {
		this.phasen = phasen;
	}

	/**
	 * 
	 * @param phase Eine einzelne Phase
	 */
	public void setSinglePhase(Phase phase) {
		this.phasen.add(phase);
	}

	/**
	 * 
	 * @return String
	 */
	public String getErsteller() {
		return ersteller.get();
	}

	/**
	 * 
	 * @return StringProperty
	 */
	public StringProperty erstellerProperty() {
		return ersteller;
	}

	/**
	 * 
	 * @param ersteller Ersteller des Projektes
	 */
	public void setErsteller(String ersteller) {
		this.ersteller = new SimpleStringProperty(ersteller);
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isAbgeschickt() {
		return abgeschickt.get();
	}

	/**
	 * 
	 * @return StringProperty
	 */
	public StringProperty abgeschicktProperty() {
		if (this.isAbgeschickt())
			return new SimpleStringProperty("Ja");
		else
			return new SimpleStringProperty("Nein");
	}

	/**
	 * 
	 * @param abgeschickt Abgeschickt Status des Projektes
	 */
	public void setAbgeschickt(boolean abgeschickt) {
		this.abgeschickt = new SimpleBooleanProperty(abgeschickt);
	}

	/**
	 * 
	 * @return String
	 */
	public String getStartDate() {
		return startDate.get();
	}

	/**
	 * 
	 * @return StringProperty
	 */
	public StringProperty startDateProperty() {
		return startDate;
	}

	/**
	 * 
	 * @param startDate Startdatum des Projektes
	 */
	public void setStartDate(String startDate) {
		this.startDate = new SimpleStringProperty(startDate);
	}

	/**
	 * 
	 * @return String
	 */
	public String getEndDate() {
		return endDate.get();
	}

	/**
	 * 
	 * @return StringProperty
	 */
	public StringProperty endDateProperty() {
		return endDate;
	}

	/**
	 * 
	 * @param endDate Enddatum des Projektes
	 */
	public void setEndDate(String endDate) {
		this.endDate = new SimpleStringProperty(endDate);
	}

	/**
	 * 
	 * @return List Kompetenz-Liste
	 */
	public List<Kompetenz> getKompetenzen() {
		return kompetenzen;
	}

	/**
	 * 
	 * @param kompetenzen Liste von Kompetenzen
	 */
	public void setKompetenzen(List<Kompetenz> kompetenzen) {
		this.kompetenzen = kompetenzen;
	}

	/**
	 * 
	 * @param kompetenz Einzelne Kompetenz
	 */
	public void setSingleKompetenz(Kompetenz kompetenz) {
		this.kompetenzen.add(kompetenz);
	}
}