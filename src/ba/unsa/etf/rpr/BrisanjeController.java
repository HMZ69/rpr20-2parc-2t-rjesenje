package ba.unsa.etf.rpr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class BrisanjeController {
    ArrayList<Grad> gradovi;
    ArrayList<Drzava> drzave;
    public ObservableList<Drzava> listDrzave;
    public ChoiceBox<Drzava> choiceDrzava;
    public CheckBox checkSaGradovima;
    public Drzava odabrana;
    public boolean saGradovima;

    public BrisanjeController(ArrayList<Drzava> drzave) {
        this.gradovi = gradovi;
        this.drzave = drzave;
        this.odabrana = null;
        listDrzave = FXCollections.observableArrayList(drzave);
    }

    @FXML
    public void initialize() {
        choiceDrzava.setItems(listDrzave);
    }

    public void actionClose(ActionEvent actionEvent) {
        Stage stage = (Stage) choiceDrzava.getScene().getWindow();
        stage.close();
    }

    public void actionObrisi(ActionEvent actionEvent) {
        odabrana = choiceDrzava.getValue();
        saGradovima = checkSaGradovima.isSelected();
        Stage stage = (Stage) choiceDrzava.getScene().getWindow();
        stage.close();
    }
}
