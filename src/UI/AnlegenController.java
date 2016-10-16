package UI;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import Datenbank.Datenbank;
import Projekt.Aufwand;
import Projekt.Kompetenz;
import Projekt.Phase;
import Projekt.Projekt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * Controller für Anlegen.fxml GUI
 * 
 * @author Tim Krießler
 */
public class AnlegenController {

	@FXML
	private TableView<Kompetenz> tbl_kompetenz;
	@FXML
	private TableView<Phase> tbl_phase;
	@FXML
	private TextField txt_kompetenz;
	@FXML
	private TextField txt_phase;
	@FXML
	private TextField txt_risikozuschlag;
	@FXML
	private TextField txt_pt_intern;
	@FXML
	private TextField txt_pt_extern;
	@FXML
	private TextField txt_mak_pt_intern;
	@FXML
	private TextField txt_mak_pt_extern;
	@FXML
	private TextField txt_mak_intern;
	@FXML
	private TextField txt_mak_extern;
	@FXML
	private Button btn_phase;
	@FXML
	private Button btn_kompetenz;
	@FXML
	private Button btn_aufwand_festlegen;
	@FXML
	private Button btn_projekt_speichern;
	@FXML
	private TableColumn<Kompetenz, String> tblCell_kompetenz;
	@FXML
	private TableColumn<Phase, String> tblCell_phase;
	@FXML
	private ChoiceBox<String> chobx_aufwand;
	@FXML
	private DatePicker dtpkr_start;
	@FXML
	private DatePicker dtpkr_end;

	// Diese Liste aktualsiert sich automatisch und damit auch die Tabelle
	private ObservableList<Kompetenz> kompetenzen = FXCollections.observableArrayList();

	// Diese Liste aktualsiert sich automatisch und damit auch die Tabelle
	private ObservableList<Phase> phasen = FXCollections.observableArrayList();

	// Diese Liste aktualsiert sich automatisch und damit auch die Tabelle
	private ObservableList<String> aufwaende = FXCollections.observableArrayList();

	// Variablen
	Projekt projekt;
	int arbeitstage = 0;
	Datenbank myDB = new Datenbank();
	boolean indexPhaseClicked = false;
	boolean indexKompetenzClicked = false;

	@FXML
	private void initialize() {
		System.out.println("Fenster wurde geöffnet.");

		// Importiere Projekt
		projekt = OpenMainPage.tmpProjekt;

		// Initalisiere Tabelle

		tblCell_kompetenz.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		tblCell_phase.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

		kompetenzen = FXCollections.observableArrayList(projekt.getKompetenzen());
		phasen = FXCollections.observableArrayList(projekt.getPhasen());

		tbl_kompetenz.setItems(kompetenzen);
		tbl_phase.setItems(phasen);

		aufwaende.add("Personentage (PT)");
		aufwaende.add("Mitarbeiterkapazität (MAK)");
		chobx_aufwand.setItems(aufwaende);
		// chobx_aufwand.getSelectionModel().selectFirst();

		txt_pt_intern.setVisible(false);
		txt_pt_extern.setVisible(false);
		txt_mak_intern.setVisible(false);
		txt_mak_extern.setVisible(false);
		txt_mak_pt_intern.setVisible(false);
		txt_mak_pt_extern.setVisible(false);

		// ActionHandler Tabelle Phase
		tbl_phase.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {

				try {
					Phase phaseSelected = tbl_phase.getSelectionModel().getSelectedItem();
					// Berechnung Arbeitstage aus Phasenzeitraum für
					// PT-Berechnung bei MAK
					String startdatum = phaseSelected.getStartDate();
					String enddatum = phaseSelected.getEndDate();

					arbeitstage = calculateDate(startdatum, enddatum);

					txt_mak_pt_intern.setText(arbeitstage + " PT");
					txt_mak_pt_extern.setText(arbeitstage + " PT");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				indexPhaseClicked = true;
				// was passiert wenn eine phase und eine kompetenz ausgewählt
				// wurden
				if (indexKompetenzClicked) {
					btn_aufwand_festlegen.setDisable(false);
					fülleFelder();
				}

			}
		});

		// ActionHandler Tabelle Kompetenz
		tbl_kompetenz.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				indexKompetenzClicked = true;
				// was passiert wenn eine phase und eine kompetenz ausgewählt
				// wurden
				if (indexPhaseClicked) {
					btn_aufwand_festlegen.setDisable(false);
					fülleFelder();
				}
			}
		});
	}

	@FXML
	public void btn_kompetenz_click(ActionEvent event) throws Exception {
		boolean vorhanden = false;
		for (Kompetenz kompetenz : kompetenzen) {
			if (kompetenz.getName().equals(txt_kompetenz.getText()))
				vorhanden = true;
		}
		if (!vorhanden) {
			if (!(txt_kompetenz.getText().equals("") || txt_kompetenz == null)) {

				// Risikozuschlag von -,% und falschem Dezimalzeichen befreien
				String risikozuschlagString = txt_risikozuschlag.getText().replaceAll("%", "");
				risikozuschlagString = risikozuschlagString.replaceAll(",", ".");
				risikozuschlagString = risikozuschlagString.replaceAll("-", "");
				Double risikozuschlag = Double.parseDouble(risikozuschlagString);

				kompetenzen.add(new Kompetenz(txt_kompetenz.getText(), risikozuschlag));
				tbl_kompetenz.setItems(kompetenzen);
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Kompetenzbezeichnung darf nicht leer sein.");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Der angegebene Kompetenzname ist bereits vorhanden!");
			alert.showAndWait();
		}

	}

	@FXML
	public void btn_phase_click(ActionEvent event) throws Exception {
		boolean vorhanden = false;
		// Prüfung ob Phase bereits vorhanden
		for (Phase phase : phasen) {
			if (phase.getName().equals(txt_phase.getText()))
				vorhanden = true;
		}
		if (!vorhanden) {
			// Prüfung ob alle Felder ausgefüllt
			if ((!(txt_phase.getText().equals("")) || txt_phase != null) && (dtpkr_start.getValue() != null)
					&& (dtpkr_end.getValue() != null) && !(txt_risikozuschlag.getText().equals(""))) {

				Phase phase = new Phase(txt_phase.getText(), dtpkr_start.getValue().toString(),
						dtpkr_end.getValue().toString());

				phasen.add(phase);

				tbl_phase.setItems(phasen);
				// TODO: Fokus auf ein Element setzen, damit Arbeitstage immer
				// berechnet werden können
			} else {
				String fehlermeldung = "";
				if (txt_risikozuschlag.getText().equals(""))
					fehlermeldung = "Risikozuschlag eingeben.";

				if ((dtpkr_start.getValue() == null) || (dtpkr_end.getValue() == null))
					fehlermeldung = "Zeitraum muss ausgewählt werden.";

				if (txt_phase.getText().equals("") || txt_phase == null)
					fehlermeldung = "Phasenbezeichnung darf nicht leer sein.";

				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(fehlermeldung);
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Der angegebene Phasenname ist bereits vorhanden!");
			alert.showAndWait();
		}

	}

	@FXML
	public void chobx_aufwand_selected(ActionEvent event) throws Exception {

		String chobx_aufwand_selection = chobx_aufwand.getValue();
		System.out.println(chobx_aufwand_selection);
		switch (chobx_aufwand_selection) {
		case "Personentage (PT)":
			txt_mak_intern.setVisible(false);
			txt_mak_extern.setVisible(false);
			txt_mak_pt_intern.setVisible(false);
			txt_mak_pt_extern.setVisible(false);
			txt_pt_intern.setVisible(true);
			txt_pt_extern.setVisible(true);
			break;
		case "Mitarbeiterkapazität (MAK)":
			txt_pt_intern.setVisible(false);
			txt_pt_extern.setVisible(false);
			txt_mak_intern.setVisible(true);
			txt_mak_extern.setVisible(true);
			txt_mak_pt_intern.setVisible(true);
			txt_mak_pt_extern.setVisible(true);
			break;

		default:
			break;
		}
	}

	@FXML
	public void datepicker_ende_selected(ActionEvent event) throws Exception {

		int startDatum = Integer.parseInt(dtpkr_start.getValue().toString().replaceAll("-", ""));
		int endDatum = Integer.parseInt(dtpkr_end.getValue().toString().replaceAll("-", ""));
		System.out.println(startDatum);
		System.out.println(endDatum);
		if (endDatum <= startDatum) {
			dtpkr_end.setValue(dtpkr_start.getValue().plusDays(1));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Das Enddatum darf nicht gleich wie das Startdatum sein oder davor liegen.");
			alert.showAndWait();
		}
	}

	@FXML
	public void btn_aufwand_festlegen_click(ActionEvent event) throws Exception {
		double ptIntern = 0;
		double ptExtern = 0;
		int auswahl = chobx_aufwand.getSelectionModel().getSelectedIndex();

		if ((auswahl == 0 && (txt_pt_intern.equals("") || txt_pt_extern.equals("")))
				|| (auswahl == 1 && (txt_mak_intern.equals("") || txt_mak_extern.equals("")))) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Bitte Aufwände für intern und extern eingeben!");
			alert.showAndWait();
		} else {
			try {
				Phase phaseSelected = tbl_phase.getSelectionModel().getSelectedItem();
				Kompetenz kompetenzSelected = tbl_kompetenz.getSelectionModel().getSelectedItem();

				switch (auswahl) {
				case 0:
					ptIntern = Double.parseDouble(txt_pt_intern.getText());
					ptExtern = Double.parseDouble(txt_pt_extern.getText());
					break;
				case 1:
					// Berechnung der PT: MAK * verfügbare Werktage der Phase
					ptIntern = Double.parseDouble(txt_mak_intern.getText()) * arbeitstage;
					ptExtern = Double.parseDouble(txt_mak_extern.getText()) * arbeitstage;
					break;
				default:
					break;
				}

				// Prüfung ob Aufwand für Kompetenz bereits vorhanden ist
				boolean aufwandVorhanden = false;
				for (Aufwand aufwand : phaseSelected.getAufwände()) {
					if (aufwand.getName().endsWith(kompetenzSelected.getName()))
						aufwandVorhanden = true;
				}
				if (!aufwandVorhanden) {
					phaseSelected.setSingleAufwand(new Aufwand("intern " + kompetenzSelected.getName()));
					phaseSelected.setSingleAufwand(new Aufwand("extern " + kompetenzSelected.getName()));
				}

				for (Aufwand aufwand : phaseSelected.getAufwände()) {
					if (aufwand.getName().startsWith("intern")
							&& aufwand.getName().endsWith(kompetenzSelected.getName())) {
						aufwand.setZugehoerigkeit(kompetenzSelected.getName());
						aufwand.setPt(ptIntern);
					}
					if (aufwand.getName().startsWith("extern")
							&& aufwand.getName().endsWith(kompetenzSelected.getName())) {
						aufwand.setZugehoerigkeit(kompetenzSelected.getName());
						aufwand.setPt(ptExtern);
					}
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@FXML
	public void btn_projekt_speichern_click(ActionEvent event) throws Exception {

		Projekt projekt = OpenMainPage.tmpProjekt;
		if (projekt != null) {
			for (Phase phase : tbl_phase.getItems()) {
				projekt.setSinglePhase(phase);
				System.out.println(phase.getName() + " wurde Projekt hinzugefügt!");
			}
			for (Kompetenz kompetenz : tbl_kompetenz.getItems()) {
				projekt.setSingleKompetenz(kompetenz);
				System.out.println(kompetenz.getName() + " wurde Projekt hinzugefügt!");
			}
			try {
				myDB.updateProjekt(projekt);
				System.out.println("Projekt gespeichert!");
			} catch (Exception e) {
				System.out.println("Speichern fehlgeschlagen!");
			}
		}
	}

	@FXML
	public void btn_zurueck_click(ActionEvent event) throws Exception {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("Vor dem Verlassen speichern?");
	}

	// berechne Anzahl der Arbeitstage zwischen zwei Daten (inklusive Start- und
	// Enddatum)
	public int calculateDate(String startDatum, String endDatum) {
		String[] datum = startDatum.split("-");
		startDatum = datum[2] + "/" + datum[1] + "/" + datum[0];
		String startDate = startDatum;

		datum = endDatum.split("-");
		endDatum = datum[2] + "/" + datum[1] + "/" + datum[0];
		String endDate = endDatum;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Calendar start = Calendar.getInstance();
			start.setTime(sdf.parse(startDate));
			Calendar end = Calendar.getInstance();
			end.setTime(sdf.parse(endDate));
			int workingDays = 0;
			while (!start.after(end)) {
				int day = start.get(Calendar.DAY_OF_WEEK);
				if ((day != Calendar.SATURDAY) && (day != Calendar.SUNDAY))
					workingDays++;
				start.add(Calendar.DATE, 1);
			}
			return workingDays;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	public void fülleFelder() {
		Phase phaseSelected = tbl_phase.getSelectionModel().getSelectedItem();
		Kompetenz kompetenzSelected = tbl_kompetenz.getSelectionModel().getSelectedItem();

		txt_pt_intern.setText("");
		txt_pt_extern.setText("");
		txt_mak_intern.setText("");
		txt_mak_extern.setText("");

		for (Aufwand aufwand : phaseSelected.getAufwände()) {
			if (aufwand.getName().startsWith("intern") && aufwand.getName().endsWith(kompetenzSelected.getName())) {
				txt_pt_intern.setText(String.valueOf(aufwand.getPt()));
				txt_mak_intern.setText(String.valueOf(aufwand.getPt() / arbeitstage));

			}
			if (aufwand.getName().startsWith("extern") && aufwand.getName().endsWith(kompetenzSelected.getName())) {
				txt_pt_extern.setText(String.valueOf(aufwand.getPt()));
				txt_mak_extern.setText(String.valueOf(aufwand.getPt() / arbeitstage));
			}
		}
	}
}