package jar.dto;

// Telo zahteve PUT /api/proracun/{kategorijaId}, npr. { "znesek": 150.00 }
public record ProracunZahteva(Double znesek) {
}
