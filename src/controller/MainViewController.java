package controller;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import datenbank.Datenbank;
import export.Excel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;
import projektDaten.Aufwand;
import projektDaten.Kompetenz;
import projektDaten.Phase;
import projektDaten.Projekt;
import ui.OpenChangeView;
import ui.OpenMainPage;
import ui.OpenStartPage;

/**
 * Controller für Anlegen.fxml GUI
 * 
 * @author Tim Krießler, Daniel Sogl
 */
public class MainViewController {

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
	private Button btn_export;
	@FXML
	private Button btn_zurueck;
	@FXML
	private Button btn_deletePhase;
	@FXML
	private Button btn_deleteKompetenz;
	@FXML
	private Button btn_sendProjekt;
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
	@FXML
	private DatePicker dtpkr_meldeDatum;
	@FXML
	private ImageView img_loadingSpinner;
	@FXML
	private ImageView img_saveBtnImg;
	@FXML
	private Label lbl_Intern;
	@FXML
	private Label lbl_Extern;

	// Diese Liste aktualsiert sich automatisch und damit auch die Tabelle
	private ObservableList<Kompetenz> kompetenzen = FXCollections.observableArrayList();

	// Diese Liste aktualsiert sich automatisch und damit auch die Tabelle
	private ObservableList<Phase> phasen = FXCollections.observableArrayList();

	// Diese Liste aktualsiert sich automatisch und damit auch die Tabelle
	private ObservableList<String> aufwaende = FXCollections.observableArrayList();

	// Variablen
	public static Projekt projekt;
	private long arbeitstage = 0;
	private static Datenbank myDB = new Datenbank();
	private boolean indexPhaseClicked = false;
	private boolean indexKompetenzClicked = false;
	public static boolean somethingChanged = false;

	@FXML
	private void initialize() {
		System.out.println("MainView wurde geöffnet.");

		// Importiere projektDaten
		projekt = OpenMainPage.tmpProjekt;

		// Initalisiere Tabelle
		tbl_kompetenz.setPlaceholder(new Label("Keine Kompetenzen angelegt"));
		tbl_phase.setPlaceholder(new Label("Keine Phasen angelegt"));
		tblCell_kompetenz.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		tblCell_phase.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

		kompetenzen = FXCollections.observableArrayList(projekt.getKompetenzen());
		phasen = FXCollections.observableArrayList(projekt.getPhasen());

		// Weise den Tabellen die Daten zu
		tbl_kompetenz.setItems(kompetenzen);
		tbl_phase.setItems(phasen);

		// UI wird initalisiert
		aufwaende.add("Personentage (PT)");
		aufwaende.add("Mitarbeiterkapazität (MAK)");
		chobx_aufwand.setItems(aufwaende);
		img_loadingSpinner.setVisible(false);
		lbl_Intern.setVisible(false);
		lbl_Extern.setVisible(false);
		btn_aufwand_festlegen.setVisible(false);

		if (projekt.isAbgeschickt())
			dtpkr_meldeDatum.setValue(LocalDate.parse(projekt.getMeldeDatum()));

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

					if (!tbl_phase.getItems().isEmpty())
						btn_deletePhase.setDisable(false);

					if (!tbl_phase.getItems().isEmpty() && !tbl_kompetenz.getItems().isEmpty()
							&& tbl_phase.getSelectionModel().getSelectedItem().getClass() == Phase.class) {
						Phase phaseSelected = tbl_phase.getSelectionModel().getSelectedItem();
						// Berechnung Arbeitstage aus Phasenzeitraum für
						// PT-Berechnung bei MAK
						String startdatum = phaseSelected.getStartDate();
						String enddatum = phaseSelected.getEndDate();

						arbeitstage = calculateDate(startdatum, enddatum);

						txt_mak_pt_intern.setText(arbeitstage + " PT");
						txt_mak_pt_extern.setText(arbeitstage + " PT");

						indexPhaseClicked = true;
						// was passiert wenn eine phase und eine kompetenz
						// ausgewählt
						// wurden
						if (indexKompetenzClicked) {
							btn_aufwand_festlegen.setDisable(false);
							fülleFelder();
						}

						if (mouseEvent.getClickCount() == 2 && (mouseEvent.getButton() == MouseButton.PRIMARY)) {
							// Überprüft ob auf einen Tabelleneintrag mit einer
							// Phase
							// geklickt wurde
							if (tbl_phase.getSelectionModel().getSelectedItem() instanceof Phase) {
								try {
									new OpenChangeView(tbl_phase.getSelectionModel().getSelectedItem());
								} catch (Exception e) {
									System.out.println(e.getMessage());
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});

		// ActionHandler Tabelle Kompetenz
		tbl_kompetenz.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				try {
					if (!tbl_kompetenz.getItems().isEmpty())
						btn_deleteKompetenz.setDisable(false);

					if (!tbl_kompetenz.getItems().isEmpty() && !tbl_phase.getItems().isEmpty()
							&& tbl_kompetenz.getSelectionModel().getSelectedItem().getClass() == Kompetenz.class) {
						indexKompetenzClicked = true;
						if (indexPhaseClicked) {
							btn_aufwand_festlegen.setDisable(false);
							fülleFelder();
						}
						// Kompetenz ändern
						// Doppelklick + Linke Maustaste
						if (mouseEvent.getClickCount() == 2 && (mouseEvent.getButton() == MouseButton.PRIMARY)) {
							// Überprüft ob auf einen Tabelleneintrag mit einer
							// Phase
							// geklickt wurde
							if (tbl_kompetenz.getSelectionModel().getSelectedItem() instanceof Kompetenz) {
								try {
									new OpenChangeView(tbl_kompetenz.getSelectionModel().getSelectedItem());
								} catch (Exception e) {
									System.out.println(e.getMessage());
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
	}

	@FXML
	public void btn_kompetenz_click(ActionEvent event) throws Exception {

		if (txt_kompetenz.getText().length() <= 120) {
			boolean vorhanden = false;
			for (Kompetenz kompetenz : kompetenzen) {
				if (kompetenz.getName().equals(txt_kompetenz.getText()))
					vorhanden = true;
			}
			if (!vorhanden) {
				if (!(txt_kompetenz.getText().equals("") || txt_kompetenz == null)
						&& !(txt_risikozuschlag.getText().equals(""))) {

					// Risikozuschlag von -,% und falschem Dezimalzeichen
					// befreien

					try {
						String risikozuschlagString = txt_risikozuschlag.getText().replaceAll("%", "");
						risikozuschlagString = risikozuschlagString.replaceAll(",", ".");
						risikozuschlagString = risikozuschlagString.replaceAll("-", "");
						Double risikozuschlag = Double.parseDouble(risikozuschlagString);

						Kompetenz kompetenz = new Kompetenz(txt_kompetenz.getText(), risikozuschlag);

						projekt.setSingleKompetenz(kompetenz);
						kompetenzen.add(kompetenz);
						tbl_kompetenz.setItems(kompetenzen);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				} else {
					String fehlermeldung = "";

					if (txt_risikozuschlag.getText().equals(""))
						fehlermeldung = "Risikozuschlag eingeben.";
					if (txt_kompetenz.getText().equals(""))
						fehlermeldung = "Kompetenzbezeichnung darf nicht leer sein.";
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText(fehlermeldung);
					alert.showAndWait();
				}
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Der angegebene Kompetenzname ist bereits vorhanden!");
				alert.showAndWait();
			}

			checkChanges();

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Kompetenzname zu lang");
			alert.showAndWait();
		}

	}

	@FXML
	public void btn_phase_click(ActionEvent event) throws Exception {

		if (txt_phase.getText().length() <= 60) {

			boolean vorhanden = false;
			// Prüfung ob Phase bereits vorhanden
			for (Phase phase : phasen) {
				if (phase.getName().equals(txt_phase.getText()))
					vorhanden = true;
			}
			if (!datepicker_ende_selected(event)) {
				if (!vorhanden) {
					// Prüfung ob alle Felder ausgefüllt
					if ((!(txt_phase.getText().equals("")) || txt_phase != null) && (dtpkr_start.getValue() != null)
							&& (dtpkr_end.getValue() != null)) {

						try {
							Phase phase = new Phase(txt_phase.getText(), dtpkr_start.getValue().toString(),
									dtpkr_end.getValue().toString());

							projekt.setSinglePhase(phase);
							phasen.add(phase);
							tbl_phase.setItems(phasen);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					} else {
						String fehlermeldung = "";

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
			checkChanges();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Phasenname zu lang");
			alert.showAndWait();
		}

	}

	@FXML
	public void chobx_aufwand_selected(ActionEvent event) throws Exception {

		String chobx_aufwand_selection = chobx_aufwand.getValue();
		System.out.println(chobx_aufwand_selection);
		switch (chobx_aufwand_selection) {
		case "Personentage (PT)":
			btn_aufwand_festlegen.setVisible(true);
			txt_mak_intern.setVisible(false);
			txt_mak_extern.setVisible(false);
			txt_mak_pt_intern.setVisible(false);
			txt_mak_pt_extern.setVisible(false);
			txt_pt_intern.setVisible(true);
			txt_pt_extern.setVisible(true);
			lbl_Intern.setVisible(true);
			lbl_Extern.setVisible(true);
			break;
		case "Mitarbeiterkapazität (MAK)":
			btn_aufwand_festlegen.setVisible(true);
			txt_pt_intern.setVisible(false);
			txt_pt_extern.setVisible(false);
			txt_mak_intern.setVisible(true);
			txt_mak_extern.setVisible(true);
			txt_mak_pt_intern.setVisible(true);
			txt_mak_pt_extern.setVisible(true);
			lbl_Intern.setVisible(true);
			lbl_Extern.setVisible(true);
			break;
		}

		if (indexPhaseClicked && indexKompetenzClicked) {
			fülleFelder();
		}
	}

	@FXML
	public boolean datepicker_ende_selected(ActionEvent event) throws Exception {

		boolean fehler = false;
		int startDatum = Integer.parseInt(dtpkr_start.getValue().toString().replaceAll("-", ""));
		int endDatum = Integer.parseInt(dtpkr_end.getValue().toString().replaceAll("-", ""));
		if (endDatum <= startDatum) {
			dtpkr_end.setValue(dtpkr_start.getValue().plusDays(1));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Das Enddatum darf nicht gleich wie das Startdatum sein oder davor liegen.");
			alert.showAndWait();
			fehler = true;
		}
		return fehler;
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

				int phasenIndex = tbl_phase.getSelectionModel().getSelectedIndex();
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
				for (Aufwand aufwand : projekt.getPhasen().get(phasenIndex).getAufwände()) {
					if (aufwand.getZugehoerigkeit().equals(kompetenzSelected.getName()))
						aufwandVorhanden = true;
				}
				if (!aufwandVorhanden) {
					projekt.getPhasen().get(phasenIndex)
							.setSingleAufwand(new Aufwand("intern", kompetenzSelected.getName()));
					projekt.getPhasen().get(phasenIndex)
							.setSingleAufwand(new Aufwand("extern", kompetenzSelected.getName()));
				}

				for (int i = 0; i < projekt.getPhasen().get(phasenIndex).getAufwände().size(); i++) {

					if (projekt.getPhasen().get(phasenIndex).getAufwände().get(i).getName().equals("intern")
							&& projekt.getPhasen().get(phasenIndex).getAufwände().get(i).getZugehoerigkeit()
									.equals(kompetenzSelected.getName())) {
						projekt.getPhasen().get(phasenIndex).getAufwände().get(i).setPt(ptIntern);
					}

					if (projekt.getPhasen().get(phasenIndex).getAufwände().get(i).getName().equals("extern")
							&& projekt.getPhasen().get(phasenIndex).getAufwände().get(i).getZugehoerigkeit()
									.equals(kompetenzSelected.getName())) {
						projekt.getPhasen().get(phasenIndex).getAufwände().get(i).setPt(ptExtern);
					}
				}

				kompetenzen = FXCollections.observableArrayList(projekt.getKompetenzen());
				phasen = FXCollections.observableArrayList(projekt.getPhasen());

				checkChanges();

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void saveProjektRemote() {
		// Der Speichervorgang in der DB wird im Hintergrund ausgeführt
		new Thread(new Runnable() {
			@Override
			public void run() {
				Datenbank db = new Datenbank();
				db.updateProjekt(projekt);
				System.out.println("Daten in der DB gespeichert!");
			}
		}).start();
	}

	@FXML
	public void btn_projekt_speichern_click(ActionEvent event) throws Exception {

		Node source = (Node) event.getSource();
		Scene scene = source.getScene();

		try {
			// Der Speichervorgang in der DB wird im Hintergrund ausgeführt
			new Thread(new Runnable() {
				@Override
				public void run() {
					img_loadingSpinner.setVisible(true);
					btn_projekt_speichern.setDisable(true);
					myDB.updateProjekt(projekt);
					System.out.println("Daten in der DB gespeichert!");
					btn_projekt_speichern.setDisable(false);
					img_loadingSpinner.setVisible(false);
				}
			}).start();
			somethingChanged = false;
			Stage stage = (Stage) scene.getWindow();
			stage.setTitle(projekt.getName());
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Speichern fehlgeschlagen!");
			alert.showAndWait();
		}
	}

	@FXML
	public void dtpkr_meldeDatum_ende_selected(ActionEvent event) throws Exception {
		System.out.println("Datum ausgewählt");
	}

	@FXML
	public void btn_sendProjekt_click(ActionEvent event) throws Exception {
		
		// Holle aktuelles Datum
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
		
		
		if(dtpkr_meldeDatum.getValue() == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Ungültiges Meldedatum");
			alert.setContentText("Bitte geben Sie ein Meldedatum an!");
			alert.showAndWait();
		} else if(Integer.parseInt(dtpkr_meldeDatum.getValue().toString().replaceAll("-", "")) > Integer.parseInt(date.format(formatter).replaceAll(" ", ""))){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Ungültiges Meldedatum");
			alert.setContentText("Meldedatum darf nicht in der Zukunft liegen!");
			alert.showAndWait();
		} else {
			Node source = (Node) event.getSource();
			Scene scene = source.getScene();
			Stage stage = (Stage) scene.getWindow();

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Projektdaten exportieren");
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xlsx", "*.xlsx"));
			File file = fileChooser.showSaveDialog(stage);

			if (file != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						btn_sendProjekt.setDisable(true);
						projekt.setAbgeschickt(true);
						projekt.setMeldeDatum(dtpkr_meldeDatum.getValue().toString());
						Excel.ExportToExcel(projekt, file.getAbsolutePath());
						try {
							btn_projekt_speichern_click(event);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						btn_sendProjekt.setDisable(false);
					}
				}).start();
			}
		}
	}

	@FXML
	public void btn_zurueck_click(ActionEvent event) throws Exception {

		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();

		if (somethingChanged) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Bitte wählen Sie eine der folgenden Auswahlmöglichkeiten!");

			ButtonType buttonTypeOne = new ButtonType("Speichern & Verlassen");
			ButtonType buttonTypeTwo = new ButtonType("Ohne speichern verlassen");
			ButtonType buttonTypeCancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == buttonTypeOne) {
				btn_projekt_speichern_click(event);
				new OpenStartPage();
				stage.close();
			} else if (result.get() == buttonTypeTwo) {
				new OpenStartPage();
				stage.close();
			}
		} else {
			new OpenStartPage();
			stage.close();
		}

	}

	// berechne Anzahl der Arbeitstage zwischen zwei Daten (inklusive Start- und
	// Enddatum)
	public long calculateDate(String startDatum, String endDatum) {
		try {
			LocalDate start = LocalDate.parse(startDatum);
			LocalDate ende = LocalDate.parse(endDatum);

			long daysBetween = ChronoUnit.DAYS.between(start, ende);
			// Arbeitstage (7-Tage-Woche) werden mit dem Faktor für den 17-Tage
			// Monat multipliziert
			double personentage = (0.607 * daysBetween);
			return Math.round(personentage);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

	@FXML
	public void btn_export_click(ActionEvent event) throws Exception {
		Node source = (Node) event.getSource();
		Scene scene = source.getScene();
		Stage stage = (Stage) scene.getWindow();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Projektdaten exportieren");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xlsx", "*.xlsx"));
		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Excel.ExportToExcel(projekt, file.getAbsolutePath());
				}
			}).start();
		}
	}

	@FXML
	public void btn_deletePhase_click(ActionEvent event) throws Exception {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("Möchten Sie die Phase: " + tbl_phase.getSelectionModel().getSelectedItem().getName()
				+ " wirklich löschen?");
		ButtonType buttonTypeOne = new ButtonType("Löschen");
		ButtonType buttonTypeCancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == buttonTypeOne) {
			try {
				projekt.getPhasen().remove(tbl_phase.getSelectionModel().getSelectedIndex());
				phasen.remove(tbl_phase.getSelectionModel().getSelectedIndex());

				if (tbl_phase.getItems().isEmpty())
					btn_deletePhase.setDisable(true);

				checkChanges();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@FXML
	public void btn_deleteKompetenz_click(ActionEvent event) throws Exception {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("Möchten Sie die Kompetenz: "
				+ tbl_kompetenz.getSelectionModel().getSelectedItem().getName() + " wirklich löschen?");
		ButtonType buttonTypeOne = new ButtonType("Löschen");
		ButtonType buttonTypeCancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == buttonTypeOne) {
			try {
				// Lösche die Aufwände in den Phasen mit der Zugehörigkeit zur
				// Kompetenz
				for (int i = 0; i < projekt.getPhasen().size(); i++) {
					for (int k = 0; k < projekt.getPhasen().get(i).getAufwände().size(); k++) {
						if (projekt.getPhasen().get(i).getAufwände().get(k).getZugehoerigkeit().equals(projekt
								.getKompetenzen().get(tbl_kompetenz.getSelectionModel().getSelectedIndex()).getName()))
							projekt.getPhasen().get(i).getAufwände().remove(k);
					}
				}
				projekt.getKompetenzen().remove(tbl_kompetenz.getSelectionModel().getSelectedIndex());
				kompetenzen.remove(tbl_kompetenz.getSelectionModel().getSelectedIndex());

				if (tbl_kompetenz.getItems().isEmpty())
					btn_deleteKompetenz.setDisable(true);

				checkChanges();

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void updateTbl_phase() {
		try {
			phasen = FXCollections.observableArrayList(projekt.getPhasen());
			checkChanges();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void updateTbl_kompetenz() {
		try {
			kompetenzen = FXCollections.observableArrayList(projekt.getKompetenzen());
			checkChanges();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void fülleFelder() {
		try {
			Phase phaseSelected = tbl_phase.getSelectionModel().getSelectedItem();
			Kompetenz kompetenzSelected = tbl_kompetenz.getSelectionModel().getSelectedItem();

			txt_pt_intern.setText("0");
			txt_pt_extern.setText("0");
			txt_mak_intern.setText("0");
			txt_mak_extern.setText("0");

			for (Aufwand aufwand : phaseSelected.getAufwände()) {
				if (aufwand.getName().equals("intern")
						&& aufwand.getZugehoerigkeit().equals(kompetenzSelected.getName())) {
					txt_pt_intern.setText(String.valueOf(aufwand.getPt()));
					txt_mak_intern.setText(String.valueOf(aufwand.getPt() / arbeitstage));

				}
				if (aufwand.getName().equals("extern")
						&& aufwand.getZugehoerigkeit().equals(kompetenzSelected.getName())) {
					txt_pt_extern.setText(String.valueOf(aufwand.getPt()));
					txt_mak_extern.setText(String.valueOf(aufwand.getPt() / arbeitstage));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void checkChanges() {
		somethingChanged = true;
		Scene scene = (Scene) txt_pt_extern.getScene();
		Stage stage = (Stage) scene.getWindow();
		stage.setTitle("* " + projekt.getName());
	}
}