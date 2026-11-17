package jar.repository;

import jar.model.Strosek;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StrosekRepository extends JpaRepository<Strosek, Long> {

    
    @Query("SELECT s FROM Strosek s LEFT JOIN s.kategorija k " +
           "WHERE LOWER(s.naziv) LIKE LOWER(CONCAT('%', :iskanje, '%')) " +
           "AND (:kategorijaId IS NULL OR k.id = :kategorijaId)")
    Page<Strosek> poisci(@Param("iskanje") String iskanje,
                         @Param("kategorijaId") Long kategorijaId,
                         Pageable pageable);

    @Query("SELECT s.kategorija.id, SUM(s.znesek) FROM Strosek s " +
           "WHERE s.datum BETWEEN :od AND :doDatuma AND s.kategorija IS NOT NULL " +
           "GROUP BY s.kategorija.id")
    List<Object[]> porabaPoKategorijah(@Param("od") LocalDate od,
                                       @Param("doDatuma") LocalDate doDatuma);
}