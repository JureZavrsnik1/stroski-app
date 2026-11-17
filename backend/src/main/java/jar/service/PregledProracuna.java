package jar.service;

import jar.dto.PregledKategorije;
import jar.dto.StanjeProracuna;
import jar.model.Kategorija;

public final class PregledProracuna {

    static final double MEJA_OPOZORILA = 0.8;

    private PregledProracuna() {
    }

    public static StanjeProracuna stanje(Double proracun, double poraba) {
        if (proracun == null) {
            return StanjeProracuna.BREZ_PRORACUNA;
        }
        if (poraba > proracun) {
            return StanjeProracuna.PREKORACEN;
        }
        if (poraba >= proracun * MEJA_OPOZORILA) {
            return StanjeProracuna.OPOZORILO;
        }
        return StanjeProracuna.V_REDU;
    }

    public static PregledKategorije vrstica(Kategorija kategorija, Double proracun, double poraba) {
        poraba = zaokrozi(poraba);
        Integer odstotek = null;
        Double preostanek = null;
        if (proracun != null) {
            odstotek = (int) Math.round(poraba / proracun * 100);
            preostanek = zaokrozi(proracun - poraba);
        }
        return new PregledKategorije(kategorija.getId(), kategorija.getNaziv(),
                proracun, poraba, odstotek, preostanek, stanje(proracun, poraba));
    }

    static double zaokrozi(double znesek) {
        return Math.round(znesek * 100) / 100.0;
    }
}
