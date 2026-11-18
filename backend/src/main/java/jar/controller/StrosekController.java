package jar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jar.model.Strosek;
import jar.repository.StrosekRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.nio.charset.StandardCharsets;



@RestController
@RequestMapping("/api/strosek")
@Tag(name = "Stroški")
public class StrosekController {

    private final StrosekRepository strosekRepository;

    public StrosekController(StrosekRepository strosekRepository) {
        this.strosekRepository = strosekRepository;
    }

    // CREATE: POST /api/strosek
    @Operation(summary = "Ustvari nov strošek")
    @ApiResponse(responseCode = "200", description = "Strošek ustvarjen")
    @PostMapping
    public Strosek createStrosek(@RequestBody Strosek strosek) {
        return strosekRepository.save(strosek);
    }

    // READ (vsi): GET /api/strosek
    @Operation(summary = "Vrne vse stroške")
    @GetMapping
    public List<Strosek> pridobiVse() {
        return strosekRepository.findAll();
    }

    // ISKANJE, FILTRIRANJE, PAGINACIJA in RAZVRŠČANJE: GET /api/strosek/iskanje
    @Operation(summary = "Išče stroške s paginacijo in razvrščanjem",
               description = "Išče po nazivu, neobvezno filtrira po kategoriji. Vrne eno stran rezultatov.")
    @GetMapping("/iskanje")
    public Page<Strosek> poisci(
            @RequestParam(defaultValue = "") String iskanje,       // besedilo v nazivu (prazno = vsi)
            @RequestParam(required = false) Long kategorijaId,     // filter po kategoriji (ni obvezen)
            @RequestParam(defaultValue = "0") int stran,           // številka strani, začne se pri 0
            @RequestParam(defaultValue = "5") int velikost,        // število zapisov na stran
            @RequestParam(defaultValue = "datum") String razvrsti, // polje za razvrščanje
            @RequestParam(defaultValue = "desc") String smer) {    // asc ali desc

        // Varovalo: dovoljena so samo ta polja, sicer bi neobstoječe polje povzročilo napako 500
        List<String> dovoljenaPolja = List.of("naziv", "znesek", "datum");
        if (!dovoljenaPolja.contains(razvrsti)) {
            razvrsti = "datum";
        }

        Sort.Direction smerRazvrscanja =
                smer.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Pageable združuje: katera stran, koliko zapisov in kako razvrstiti
        Pageable pageable = PageRequest.of(stran, velikost, Sort.by(smerRazvrscanja, razvrsti));

        return strosekRepository.poisci(iskanje, kategorijaId, pageable);
    }

    // IZVOZ V CSV: GET /api/strosek/izvoz
    // Isti filter kot pri iskanju, vendar brez paginacije (izvozijo se vsi zadetki)
    @Operation(summary = "Izvozi stroške v CSV",
               description = "Vrne vse stroške, ki ustrezajo iskanju in kategoriji. Če ni zadetkov, vrne 204.")
    @GetMapping("/izvoz")
    public ResponseEntity<String> izvoziCsv(
            @RequestParam(defaultValue = "") String iskanje,
            @RequestParam(required = false) Long kategorijaId) {

        // Vsi zadetki (ne le ena stran), najnovejši najprej
        List<Strosek> stroski = strosekRepository
                .poisci(iskanje, kategorijaId, Pageable.unpaged(Sort.by(Sort.Direction.DESC, "datum")))
                .getContent();

        // Ni zadetkov: status 204, brez vsebine
        if (stroski.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Naslovna vrstica in po ena vrstica za vsak strošek (ločilo je podpičje)
        StringBuilder csv = new StringBuilder("naziv;znesek;datum;kategorija\n");
        for (Strosek strosek : stroski) {
            String kategorija = strosek.getKategorija() != null ? strosek.getKategorija().getNaziv() : "";
            csv.append(csvVrednost(strosek.getNaziv())).append(';')
               .append(strosek.getZnesek()).append(';')
               .append(strosek.getDatum()).append(';')
               .append(csvVrednost(kategorija)).append('\n');
        }

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.toString());
    }

    // Vrednost s podpičjem ali narekovajem obdamo z narekovaji
    private String csvVrednost(String vrednost) {
        if (vrednost == null) {
            return "";
        }
        if (vrednost.contains(";") || vrednost.contains("\"") || vrednost.contains("\n")) {
            return "\"" + vrednost.replace("\"", "\"\"") + "\"";
        }
        return vrednost;
    }

    // READ (en): GET /api/strosek/{id}
    @Operation(summary = "Vrne en strošek po id")
    @ApiResponse(responseCode = "200", description = "Najden")
    @ApiResponse(responseCode = "404", description = "Ni najden")
    @GetMapping("/{id}")
    public ResponseEntity<Strosek> pridobiEnega(@PathVariable Long id) {
        return strosekRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE: PUT /api/strosek/{id}
    @Operation(summary = "Posodobi strošek")
    @ApiResponse(responseCode = "200", description = "Posodobljen")
    @ApiResponse(responseCode = "404", description = "Ni najden")
    @PutMapping("/{id}")
    public ResponseEntity<Strosek> posodobi(@PathVariable Long id, @RequestBody Strosek novi) {
        return strosekRepository.findById(id)
                .map(obstojeci -> {
                    obstojeci.setNaziv(novi.getNaziv());
                    obstojeci.setOpomba(novi.getOpomba());
                    obstojeci.setZnesek(novi.getZnesek());
                    obstojeci.setDatum(novi.getDatum());
                    obstojeci.setKategorija(novi.getKategorija());
                    return ResponseEntity.ok(strosekRepository.save(obstojeci));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: DELETE /api/strosek/{id}
    @Operation(summary = "Izbriše strošek")
    @ApiResponse(responseCode = "204", description = "Izbrisan")
    @ApiResponse(responseCode = "404", description = "Ni najden")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> izbrisi(@PathVariable Long id) {
        if (!strosekRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        strosekRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}