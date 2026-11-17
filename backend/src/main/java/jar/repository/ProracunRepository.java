package jar.repository;

import jar.model.Proracun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProracunRepository extends JpaRepository<Proracun, Long> {

    Optional<Proracun> findByKategorijaId(Long kategorijaId);
}
