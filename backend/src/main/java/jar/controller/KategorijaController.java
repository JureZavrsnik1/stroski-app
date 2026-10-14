package jar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jar.model.Kategorija;
import jar.repository.KategorijaRepository;
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
@RequestMapping("/api/kategorija")
@Tag(name = "Kategorije", description = "Upravljanje kategorij stroškov (CRUD operacije)")
public class KategorijaController {

    private final KategorijaRepository kategorijaRepository;

    public KategorijaController(KategorijaRepository kategorijaRepository) {
        this.kategorijaRepository = kategorijaRepository;
    }

    // CREATE
    @Operation(summary = "Ustvari novo kategorijo")
    @PostMapping
    public Kategorija ustvari(@RequestBody Kategorija kategorija) {
        return kategorijaRepository.save(kategorija);
    }

    // READ vse
    @Operation(summary = "Vrne vse kategorije")
    @GetMapping
    public List<Kategorija> pridobiVse() {
        return kategorijaRepository.findAll();
    }

    // READ ena
    @Operation(summary = "Vrne eno kategorijo po ID-ju")
    @ApiResponse(responseCode = "404", description = "Kategorija ne obstaja")
    @GetMapping("/{id}")
    public ResponseEntity<Kategorija> pridobiEno(@PathVariable Long id) {
        Optional<Kategorija> rezultat = kategorijaRepository.findById(id);
        if (rezultat.isPresent()) {
            return ResponseEntity.ok(rezultat.get());
        }
        return ResponseEntity.notFound().build();
    }

    // UPDATE
    @Operation(summary = "Posodobi kategorijo")
    @ApiResponse(responseCode = "404", description = "Kategorija ne obstaja")
    @PutMapping("/{id}")
    public ResponseEntity<Kategorija> posodobi(@PathVariable Long id, @RequestBody Kategorija nova) {
        Optional<Kategorija> rezultat = kategorijaRepository.findById(id);
        if (rezultat.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Kategorija obstojeca = rezultat.get();
        obstojeca.setNaziv(nova.getNaziv());
        return ResponseEntity.ok(kategorijaRepository.save(obstojeca));
    }

    // DELETE
    @Operation(summary = "Izbriše kategorijo",
               description = "Kategorije, ki jo uporablja kakšen strošek, ni mogoče izbrisati.")
    @ApiResponse(responseCode = "204", description = "Kategorija je izbrisana")
    @ApiResponse(responseCode = "404", description = "Kategorija ne obstaja")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> izbrisi(@PathVariable Long id) {
        if (!kategorijaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        kategorijaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}