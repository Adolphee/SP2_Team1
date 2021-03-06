package gui;

import database.LogDAO;
import database.VaardigheidDAO;
import email.Email;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import logic.Vaardigheid;

public class AanvragenController {

	@FXML
	TableView<Vaardigheid> aanvragen;
	@FXML
	TableColumn<Vaardigheid, Integer> colId;
	@FXML
	TableColumn<Vaardigheid, String> colNaam;
	@FXML
	TableColumn<Vaardigheid, String> colOpleiding;
	@FXML
	TableColumn<Vaardigheid, String> colVan;
	@FXML
	TableColumn<Vaardigheid, String> colTot;
	@FXML
	CheckBox cbSelecteerAlles;
	@FXML
	Button bGoedkeuren;
	@FXML
	Button bAfkeuren;
	@FXML
	Label lCheck;

	@FXML
	private void clearLabel() {
		lCheck.setText("");
	}

	@FXML
	private void handleSelecteerAlles() {
		if (cbSelecteerAlles.isSelected())
			aanvragen.getSelectionModel().selectAll();
		else
			aanvragen.getSelectionModel().clearSelection();
	}

	@FXML
	private void handleCheck(ActionEvent e) {
		if (aanvragen.getSelectionModel().getSelectedItems().size() == 0) {
			lCheck.setStyle("-fx-text-fill: red");
			lCheck.setText("Geen aanvragen geselecteerd.");
		} else {

			if (e.getTarget() == bGoedkeuren) {
				for (Vaardigheid v : aanvragen.getSelectionModel().getSelectedItems())
					v.setChecked(true);

			} else {
				for (Vaardigheid v : aanvragen.getSelectionModel().getSelectedItems())
					v.setChecked(false);
			}

			if (VaardigheidDAO.updateObservables(aanvragen.getSelectionModel().getSelectedItems())) {
				LogDAO.aanvragenGekeurd(aanvragen.getSelectionModel().getSelectedItems());
				Email.aanvraag(aanvragen.getSelectionModel().getSelectedItems());
				initialize();
				lCheck.setStyle("-fx-text-fill: black");
				lCheck.setText(
						"De aanvragen werden " + (e.getTarget() == bGoedkeuren ? "goedgekeurd" : "afgekeurd") + ".");
			} else {
				lCheck.setStyle("-fx-text-fill: red");
				lCheck.setText("Er is een technische fout opgelopen.");
			}

		}

	}

	@FXML
	public void initialize() {

		aanvragen.setPlaceholder(new Label("Er zijn momenteel geen aanvragen."));
		colId.setCellValueFactory(new Callback<CellDataFeatures<Vaardigheid, Integer>, ObservableValue<Integer>>() {
			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Vaardigheid, Integer> data) {
				return new SimpleIntegerProperty(new Integer(data.getValue().getPersoneel().getPersId())).asObject();
			}
		});
		colNaam.setCellValueFactory(new Callback<CellDataFeatures<Vaardigheid, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Vaardigheid, String> data) {
				return new SimpleStringProperty(data.getValue().getPersoneel().getVolleNaam());
			}
		});
		colOpleiding
				.setCellValueFactory(new Callback<CellDataFeatures<Vaardigheid, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Vaardigheid, String> data) {
						return new SimpleStringProperty(data.getValue().getEvent().getOpleiding().getNaam());
					}
				});
		colVan.setCellValueFactory(new Callback<CellDataFeatures<Vaardigheid, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Vaardigheid, String> data) {
				return new SimpleStringProperty(data.getValue().getEvent().getStringStartdatum());
			}
		});
		colTot.setCellValueFactory(new Callback<CellDataFeatures<Vaardigheid, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Vaardigheid, String> data) {
				return new SimpleStringProperty(data.getValue().getEvent().getStringEinddatum());
			}
		});

		aanvragen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ObservableList<Vaardigheid> list = FXCollections.observableArrayList(VaardigheidDAO.getAanvragen());
		aanvragen.setItems(list);

	}
}
