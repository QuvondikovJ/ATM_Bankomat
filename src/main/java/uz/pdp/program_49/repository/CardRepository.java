package uz.pdp.program_49.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.program_49.entity.Card;
import uz.pdp.program_49.entity.CardType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    boolean existsByUsernameAndCardTypeIdAndActive(String username, Integer cardType_id, boolean active);

    Page<Card> getByBankIdAndActive(Integer bank_id, boolean active, Pageable pageable);

    Page<Card> getByCardTypeIdAndActive(Integer cardType_id, boolean active, Pageable pageable);

    List<Card> getByClientIdAndActive(UUID client_id, boolean active);

    Optional<Card> findByUsernameAndActive(String username, boolean active);
}
