package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.SQLException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGlavnaTest {
    Stage theStage;
    GlavnaController ctrl;
    GeografijaDAO dao = GeografijaDAO.getInstance();

    @Start
    public void start (Stage stage) throws Exception {
        dao.vratiBazuNaDefault();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        ctrl = new GlavnaController();
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Gradovi svijeta");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();

        stage.toFront();

        theStage = stage;
    }

    @BeforeEach
    public void resetujBazu() throws SQLException {
        dao.vratiBazuNaDefault();
    }

    @AfterEach
    public void zatvoriProzor(FxRobot robot) {
        if (robot.lookup("#btnCancel").tryQuery().isPresent())
            robot.clickOn("#btnCancel");
    }

    @Test
    public void testOdustani(FxRobot robot) {
        robot.clickOn("Brisanje države");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        // Uzimamo Stage kako bismo se uvjerili da prestaje biti vidljiv
        ChoiceBox choiceDrzava = robot.lookup("#choiceDrzava").queryAs(ChoiceBox.class);
        Stage stage = (Stage) choiceDrzava.getScene().getWindow();

        robot.clickOn("Odustani");

        assertFalse(stage.isShowing());

        robot.clickOn("Brisanje države");
        robot.lookup("#choiceDrzava").tryQuery().isPresent();
        robot.clickOn("Odustani");

        assertEquals(3, dao.drzave().size());
    }

    @Test
    public void testObrisiPraznu(FxRobot robot) {
        // Dodajemo jednu praznu državu
        robot.clickOn("Dodaj državu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Proba");
        robot.clickOn("Ok");

        assertEquals(4, dao.drzave().size());
        boolean foundProba = false;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Proba"))
                foundProba = true;
        assertTrue(foundProba);

        robot.clickOn("Brisanje države");
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        Platform.runLater(() -> theStage.hide());

        robot.clickOn("#choiceDrzava");
        robot.clickOn("Proba");
        robot.clickOn("Obriši državu");

        Platform.runLater(() -> theStage.show());

        assertEquals(3, dao.drzave().size());
        foundProba = false;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Proba"))
                foundProba = true;
        assertFalse(foundProba);
    }

    @Test
    public void testNemoguceBrisanje(FxRobot robot) {
        robot.clickOn("Brisanje države");
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        Platform.runLater(() -> theStage.hide());

        robot.clickOn("#choiceDrzava");
        robot.clickOn("Austrija");
        robot.clickOn("Obriši državu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        Platform.runLater(() -> theStage.show());

        assertEquals(3, dao.drzave().size());
        boolean foundAustrija = false;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Austrija"))
                foundAustrija = true;
        assertTrue(foundAustrija);
    }

    @Test
    public void testObrisiPraznuSaGradovima(FxRobot robot) {
        // Dodajemo jednu praznu državu
        robot.clickOn("Dodaj državu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Proba");
        robot.clickOn("Ok");

        assertEquals(4, dao.drzave().size());
        boolean foundProba = false;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Proba"))
                foundProba = true;
        assertTrue(foundProba);

        robot.clickOn("Brisanje države");
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        Platform.runLater(() -> theStage.hide());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("#choiceDrzava");
        robot.clickOn("Proba");
        robot.clickOn("#checkSaGradovima");
        robot.clickOn("Obriši državu");

        Platform.runLater(() -> theStage.show());

        assertEquals(3, dao.drzave().size());
        foundProba = false;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Proba"))
                foundProba = true;
        assertFalse(foundProba);
    }

    @Test
    public void testObrisiSaGradovima(FxRobot robot) {
        robot.clickOn("Brisanje države");
        robot.lookup("#choiceDrzava").tryQuery().isPresent();

        Platform.runLater(() -> theStage.hide());

        robot.clickOn("#choiceDrzava");
        robot.clickOn("Austrija");
        robot.clickOn("#checkSaGradovima");
        robot.clickOn("Obriši državu");

        Platform.runLater(() -> theStage.show());

        assertEquals(2, dao.drzave().size());
        boolean foundAustrija = false;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Austrija"))
                foundAustrija = true;
        assertFalse(foundAustrija);
    }

    @Test
    public void testObrisiNovuSaGradovima(FxRobot robot) {
        // Dodajemo jednu praznu državu
        robot.clickOn("Dodaj državu");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Mađarska");
        robot.clickOn("Ok");

        assertEquals(4, dao.drzave().size());
        boolean foundProba = false;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Mađarska"))
                foundProba = true;
        assertTrue(foundProba);

        // Dodajemo grad
        robot.clickOn("Dodaj grad");

        Platform.runLater(() -> theStage.hide());

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        robot.clickOn("#fieldNaziv");
        robot.write("Budimpešta");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("1756000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Mađarska");
        robot.clickOn("Ok");

        Platform.runLater(() -> theStage.show());

        foundProba = false;
        for(Grad grad : dao.gradovi())
            if (grad.getNaziv().equals("Budimpešta"))
                foundProba = true;
        assertTrue(foundProba);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.clickOn("Brisanje države");
        robot.lookup("#choiceDrzava").tryQuery().isPresent();
        Platform.runLater(() -> theStage.hide());

        robot.clickOn("#choiceDrzava");
        robot.clickOn("Mađarska");
        robot.clickOn("#checkSaGradovima");
        robot.clickOn("Obriši državu");

        Platform.runLater(() -> theStage.show());

        assertEquals(3, dao.drzave().size());
        foundProba = false;
        for(Drzava drzava : dao.drzave())
            if (drzava.getNaziv().equals("Mađarska"))
                foundProba = true;
        assertFalse(foundProba);

        foundProba = false;
        for(Grad grad : dao.gradovi())
            if (grad.getNaziv().equals("Budimpešta"))
                foundProba = true;
        assertFalse(foundProba);
    }
}
