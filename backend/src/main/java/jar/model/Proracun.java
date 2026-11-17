package jar.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

// Mesečni proračun za eno kategorijo (US1). Velja za vsak mesec enako.
@Entity
public class Proracun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // Ena kategorija ima največ en proračun
    @OneToOne
    @JoinColumn(name = "kategorija_id", unique = true, nullable = false)
    private Kategorija kategorija;

    private double znesek;

    public Proracun() {
    }

    public Proracun(Kategorija kategorija, double znesek) {
        this.kategorija = kategorija;
        this.znesek = znesek;
    }

    public Long getId() {
        return id;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public double getZnesek() {
        return znesek;
    }

    public void setZnesek(double znesek) {
        this.znesek = znesek;
    }
}
