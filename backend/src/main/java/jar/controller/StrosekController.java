package jar.controller;

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

public class StrosekController {

    private final StrosekRepository strosekRepository;

    public StrosekController(StrosekRepository strosekRepository) {
        this.strosekRepository = strosekRepository;
    }

    @PostMapping
    public Strosek createStrosek(@RequestBody Strosek strosek) {
        return strosekRepository.save(strosek);
    }

    @GetMapping
    public List<Strosek> pridobiVse() {
        return strosekRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Strosek> pridobiEnega(@PathVariable Long id) {
        Optional<Strosek> rezultat = strosekRepository.findById(id);
        if (rezultat.isPresent()) {
            return ResponseEntity.ok(rezultat.get());
        }
        return ResponseEntity.notFound().build();
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> izbrisi(@PathVariable Long id) {
        if (!strosekRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        strosekRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
