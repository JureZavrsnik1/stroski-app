package jar.service;

import jar.dto.PregledKategorije;
import jar.dto.StanjeProracuna;
import jar.model.Kategorija;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PregledProracunaTest {

    @Test
    void pod80OdstotkiJeVRedu() {
        assertEquals(StanjeProracuna.V_REDU, PregledProracuna.stanje(100.0, 79.0));
    }

    @Test
    void pri80OdstotkihJeOpozorilo() {
        assertEquals(StanjeProracuna.OPOZORILO, PregledProracuna.stanje(100.0, 80.0));
    }

    @Test
    void pri100OdstotkihJeSeOpozorilo() {
        assertEquals(StanjeProracuna.OPOZORILO, PregledProracuna.stanje(100.0, 100.0));
    }

    @Test
    void nad100OdstotkiJePrekoracen() {
        assertEquals(StanjeProracuna.PREKORACEN, PregledProracuna.stanje(100.0, 101.0));
    }

    @Test
    void brezProracuna() {
        assertEquals(StanjeProracuna.BREZ_PRORACUNA, PregledProracuna.stanje(null, 30.0));
    }

    @Test
    void vrsticaPodMejo() {
        PregledKategorije v = PregledProracuna.vrstica(kategorija("Hrana"), 150.0, 90.0);
        assertEquals(60, v.odstotek());
        assertEquals(60.0, v.preostanek());
        assertEquals(StanjeProracuna.V_REDU, v.stanje());
    }

    @Test
    void vrsticaOpozorilo() {
        PregledKategorije v = PregledProracuna.vrstica(kategorija("Hrana"), 150.0, 125.0);
        assertEquals(83, v.odstotek());
        assertEquals(StanjeProracuna.OPOZORILO, v.stanje());
    }

    @Test
    void vrsticaPrekoracen() {
        PregledKategorije v = PregledProracuna.vrstica(kategorija("Hrana"), 150.0, 170.0);
        assertEquals(-20.0, v.preostanek());
        assertEquals(StanjeProracuna.PREKORACEN, v.stanje());
    }

    @Test
    void vrsticaBrezProracuna() {
        PregledKategorije v = PregledProracuna.vrstica(kategorija("Prevoz"), null, 30.0);
        assertNull(v.odstotek());
        assertNull(v.preostanek());
        assertEquals(30.0, v.poraba());
        assertEquals(StanjeProracuna.BREZ_PRORACUNA, v.stanje());
    }

    private static Kategorija kategorija(String naziv) {
        Kategorija k = new Kategorija();
        k.setNaziv(naziv);
        return k;
    }
}
