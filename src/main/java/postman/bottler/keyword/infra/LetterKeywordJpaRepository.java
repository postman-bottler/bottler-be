package postman.bottler.keyword.infra;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import postman.bottler.keyword.infra.entity.LetterKeywordEntity;

public interface LetterKeywordJpaRepository extends JpaRepository<LetterKeywordEntity, Long> {
    @Modifying
    @Query(value = "UPDATE letter_keyword SET is_deleted = false WHERE letter_id IN :letterIds", nativeQuery = true)
    void updateIsDeleted(@Param("letterIds") List<Long> letterIds);
}
