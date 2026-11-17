package jar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jar.dto.PregledKategorije;
import jar.dto.ProracunZahteva;
import jar.model.Kategorija;
import jar.model.Proracun;
import jar.repository.KategorijaRepository;
import jar.repository.ProracunRepository;
import jar.repository.StrosekRepository;
import jar.service.PregledProracuna;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/proracun")
@Tag(name = "Proračuni", description = "Mesečni proračuni po kategorijah in pregled porabe glede na proračun (US1)")
public class ProracunController {

    static final String NAPAKA_ZNESEK = "Znesek proračuna mora biti večji od 0 €";

    private final ProracunRepository proracunRepository;
    private final KategorijaRepository kategorijaRepository;
    private final StrosekRepository strosekRepository;

    public ProracunController(ProracunRepository proracunRepository,
                              KategorijaRepository kategorijaRepository,
                              StrosekRepository strosekRepository) {
        this.proracunRepository = proracunRepository;
        this.kategorijaRepository = kategorijaRepository;
        this.strosekRepository = strosekRepository;
    }

    @Operation(summary = "Vrne vse proračune")
    @GetMapping
    public List<Proracun> pridobiVse() {
        return proracunRepository.findAll();
    }

    @Operation(summary = "Nastavi ali spremeni proračun kategorije",
               description = "Če kategorija proračuna še nima, ga ustvari, sicer obstoječega posodobi.")
    @ApiResponse(responseCode = "200", description = "Proračun shranjen")
    @ApiResponse(responseCode = "400", description = "Znesek ni večji od 0")
    @ApiResponse(responseCode = "404", description = "Kategorija ne obstaja")
    @PutMapping("/{kategorijaId}")
    public ResponseEntity<?> nastavi(@PathVariable Long kategorijaId, @RequestBody ProracunZahteva zahteva) {
        if (zahteva == null || zahteva.znesek() == null || zahteva.znesek() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("napaka", NAPAKA_ZNESEK));
        }
        Optional<Kategorija> kategorija = kategorijaRepository.findById(kategorijaId);
        if (kategorija.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Proracun proracun = proracunRepository.findByKategorijaId(kategorijaId)
                .orElse(new Proracun(kategorija.get(), 0));
        proracun.setZnesek(zahteva.znesek());
        return ResponseEntity.ok(proracunRepository.save(proracun));
    }

    @Operation(summary = "Odstrani proračun kategorije")
    @ApiResponse(responseCode = "204", description = "Proračun odstranjen")
    @ApiResponse(responseCode = "404", description = "Kategorija nima proračuna")
    @DeleteMapping("/{kategorijaId}")
    public ResponseEntity<Void> odstrani(@PathVariable Long kategorijaId) {
        Optional<Proracun> proracun = proracunRepository.findByKategorijaId(kategorijaId);
        if (proracun.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        proracunRepository.delete(proracun.get());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Poraba glede na proračun za izbrani mesec",
               description = "Za vsako kategorijo vrne porabo v mesecu, proračun, odstotek, preostanek in stanje "
                       + "(V_REDU, OPOZORILO, PREKORACEN, BREZ_PRORACUNA).")
    @ApiResponse(responseCode = "200", description = "Pregled po kategorijah")
    @ApiResponse(responseCode = "400", description = "Neveljaven mesec")
    @GetMapping("/pregled")
    public ResponseEntity<?> pregled(@RequestParam int leto, @RequestParam int mesec) {
        if (mesec < 1 || mesec > 12) {
            return ResponseEntity.badRequest().body(Map.of("napaka", "Mesec mora biti med 1 in 12"));
        }
        YearMonth izbraniMesec = YearMonth.of(leto, mesec);

        Map<Long, Double> poraba = new HashMap<>();
        for (Object[] vrstica : strosekRepository.porabaPoKategorijah(
                izbraniMesec.atDay(1), izbraniMesec.atEndOfMonth())) {
            poraba.put((Long) vrstica[0], ((Number) vrstica[1]).doubleValue());
        }

        Map<Long, Double> proracuni = new HashMap<>();
        for (Proracun p : proracunRepository.findAll()) {
            proracuni.put(p.getKategorija().getId(), p.getZnesek());
        }

        List<PregledKategorije> rezultat = kategorijaRepository.findAll(Sort.by("naziv")).stream()
                .map(k -> PregledProracuna.vrstica(k,
                        proracuni.get(k.getId()),
                        poraba.getOrDefault(k.getId(), 0.0)))
                .toList();
        return ResponseEntity.ok(rezultat);
    }
}
