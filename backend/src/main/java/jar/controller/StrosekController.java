package jar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jar.model.Strosek;
import jar.repository.StrosekRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/strosek")
@Tag(name = "Stroški", description = "Upravljanje stroškov (CRUD operacije)")
public class StrosekController {

    private final StrosekRepository strosekRepository;

    public StrosekController(StrosekRepository strosekRepository) {
        this.strosekRepository = strosekRepository;
    }

    // CREATE: POST 
    @Operation(summary = "Ustvari nov strošek",
               description = "Shrani nov strošek v bazo. Polja id ne pošiljaj, ker ga dodeli baza.")
    @ApiResponse(responseCode = "200", description = "Strošek je shranjen")
    @PostMapping
    public Strosek createStrosek(@RequestBody Strosek strosek) {
        return strosekRepository.save(strosek);
    }

    // READ vsi: GET 
    @Operation(summary = "Vrne vse stroške")
    @ApiResponse(responseCode = "200", description = "Seznam vseh stroškov")
    @GetMapping
    public List<Strosek> pridobiVse() {
        return strosekRepository.findAll();
    }

    // READ en: GET 
    @Operation(summary = "Vrne en strošek po ID-ju")
    @ApiResponse(responseCode = "200", description = "Strošek je bil najden")
    @ApiResponse(responseCode = "404", description = "Strošek s tem ID-jem ne obstaja")
    @GetMapping("/{id}")
    public ResponseEntity<Strosek> pridobiEnega(@PathVariable Long id) {
        Optional<Strosek> rezultat = strosekRepository.findById(id);
        if (rezultat.isPresent()) {
            return ResponseEntity.ok(rezultat.get());
        }
        return ResponseEntity.notFound().build();
    }

    // UPDATE: PUT 
    @Operation(summary = "Posodobi obstoječi strošek",
               description = "Prepiše vsa polja stroška z vrednostmi iz telesa zahteve.")
    @ApiResponse(responseCode = "200", description = "Strošek je posodobljen")
    @ApiResponse(responseCode = "404", description = "Strošek s tem ID-jem ne obstaja")
    @PutMapping("/{id}")
    public ResponseEntity<Strosek> posodobi(@PathVariable Long id, @RequestBody Strosek novi) {
        Optional<Strosek> rezultat = strosekRepository.findById(id);
        if (rezultat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Strosek obstojece = rezultat.get();
        obstojece.setNaziv(novi.getNaziv());
        obstojece.setOpomba(novi.getOpomba());
        obstojece.setZnesek(novi.getZnesek());
        obstojece.setDatum(novi.getDatum());
        obstojece.setKategorija(novi.getKategorija());
        return ResponseEntity.ok(strosekRepository.save(obstojece));
    }

    // DELETE: DELETE
    @Operation(summary = "Izbriše strošek")
    @ApiResponse(responseCode = "204", description = "Strošek je izbrisan")
    @ApiResponse(responseCode = "404", description = "Strošek s tem ID-jem ne obstaja")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> izbrisi(@PathVariable Long id) {
        if (!strosekRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        strosekRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}