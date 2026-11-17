package jar.dto;

public record PregledKategorije(
        Long kategorijaId,
        String kategorija,
        Double proracun,
        double poraba,
        Integer odstotek,
        Double preostanek,
        StanjeProracuna stanje) {
}
