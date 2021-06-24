package ba.unsa.etf.rpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

// Testovi za novu metodu obrisiDrzavu sa dva parametra

public class IspitGeografijaDAOTest {
    GeografijaDAO dao = GeografijaDAO.getInstance();

    @BeforeEach
    public void resetujBazu() throws SQLException {
        dao.vratiBazuNaDefault();
    }

    @Test
    void testObrisiPraznu() {
        Grad london = dao.nadjiGrad("London");
        Drzava d = new Drzava(1, "Bosna i Hercegovina", london);
        dao.dodajDrzavu(d);

        // Uzimam drugu verziju za BiH
        Drzava bih = dao.nadjiDrzavu("Bosna i Hercegovina");
        dao.obrisiDrzavu(bih, false);

        // Sada bi traženje trebalo biti neuspješno
        Drzava bih2 = dao.nadjiDrzavu("Bosna i Hercegovina");
        assertNull(bih2);
    }

    @Test
    void testObrisiPraznuSaGradovima() {
        Grad london = dao.nadjiGrad("London");
        Drzava d = new Drzava(1, "Bosna i Hercegovina", london);
        dao.dodajDrzavu(d);

        // Uzimam drugu verziju za BiH
        Drzava bih = dao.nadjiDrzavu("Bosna i Hercegovina");
        dao.obrisiDrzavu(bih, true);

        // Sada bi traženje trebalo biti neuspješno
        Drzava bih2 = dao.nadjiDrzavu("Bosna i Hercegovina");
        assertNull(bih2);
    }

    @Test
    void testNeuspjesnoBrisanje() {
        // Testiramo neujspješno brisanje postojeće države Velika Britanija
        Drzava vb = dao.nadjiDrzavu("Velika Britanija");
        assertThrows(
                NemoguceBrisanjeException.class,
                () -> dao.obrisiDrzavu(vb, false),
                "Nije moguće obrisati državu jer sadrži gradove: London, Manchester,"
        );

        // Brisanje nije uspjelo
        Drzava vb2 = dao.nadjiDrzavu("Velika Britanija");
        assertNotNull(vb2);
    }

    @Test
    void testNeuspjesnoBrisanjeNova() {
        // Testiramo neuspješno brisanje nove države BiH
        Grad london = dao.nadjiGrad("London");
        Drzava d = new Drzava(1, "Bosna i Hercegovina", london);
        dao.dodajDrzavu(d);

        // Uzimam drugu verziju za BiH da bi bio pokupljen korektan ID
        Drzava bih = dao.nadjiDrzavu("Bosna i Hercegovina");
        Grad sarajevo = new Grad(0, "Sarajevo", 35000, bih);
        dao.dodajGrad(sarajevo);

        assertThrows(
                NemoguceBrisanjeException.class,
                () -> dao.obrisiDrzavu(bih, false),
                "Nije moguće obrisati državu jer sadrži gradove: Sarajevo,"
        );

        // Brisanje nije uspjelo
        Drzava bih2 = dao.nadjiDrzavu("Bosna i Hercegovina");
        assertNotNull(bih2);
    }


    @Test
    void testObrisiSaGradovima() {
        // Testiramo brisanje postojeće države Velika Britanija sa gradovima
        Drzava vb = dao.nadjiDrzavu("Velika Britanija");
        dao.obrisiDrzavu(vb, true);

        // Brisanje je uspjelo
        Drzava vb2 = dao.nadjiDrzavu("Velika Britanija");
        assertNull(vb2);

        // Nema ni gradova
        boolean gradoviObrisani = true;
        for(Grad grad : dao.gradovi())
            if (grad.getNaziv().equals("London") || grad.getNaziv().equals("Manchester"))
                gradoviObrisani = false;
        assertTrue(gradoviObrisani);
    }

    @Test
    void testObrisiSaGradovimaNova() {
        // Testiramo brisanje nove države BiH sa gradovima
        Grad london = dao.nadjiGrad("London");
        Drzava d = new Drzava(1, "Bosna i Hercegovina", london);
        dao.dodajDrzavu(d);

        // Uzimam drugu verziju za BiH da bi bio pokupljen korektan ID
        Drzava bih = dao.nadjiDrzavu("Bosna i Hercegovina");
        Grad sarajevo = new Grad(0, "Sarajevo", 35000, bih);
        dao.dodajGrad(sarajevo);

        dao.obrisiDrzavu(bih, true);

        // Brisanje je uspjelo
        Drzava bih2 = dao.nadjiDrzavu("Bosna i Hercegovina");
        assertNull(bih2);

        // Nema ni Sarajeva
        Grad sarajevo2 = dao.nadjiGrad("Sarajevo");
        assertNull(sarajevo2);
    }


}
