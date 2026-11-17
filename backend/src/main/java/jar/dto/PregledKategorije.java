package jar.dto;

// Ena vrstica pregleda porabe glede na proračun za izbrani mesec.
// proracun, odstotek in preostanek so null, če kategorija nima proračuna.
public record PregledKategorije(
        Long kategorijaId,
        String kategorija,
        Double proracun,
        double poraba,
        Integer odstotek,
        Double preostanek,
        StanjeProracuna stanje) {
}
