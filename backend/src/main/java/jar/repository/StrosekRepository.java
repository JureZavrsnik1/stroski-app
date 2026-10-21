package jar.repository;

import jar.model.Strosek;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StrosekRepository extends JpaRepository<Strosek, Long> {

    // LEFT JOIN, da se vrnejo tudi stroški brez kategorije
    @Query("SELECT s FROM Strosek s LEFT JOIN s.kategorija k " +
           "WHERE LOWER(s.naziv) LIKE LOWER(CONCAT('%', :iskanje, '%')) " +
           "AND (:kategorijaId IS NULL OR k.id = :kategorijaId)")
    Page<Strosek> poisci(@Param("iskanje") String iskanje,
                         @Param("kategorijaId") Long kategorijaId,
                         Pageable pageable);
}