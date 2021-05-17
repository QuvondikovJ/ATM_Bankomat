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
    boolean existsByUsernameAndCardTypeId(String username, Integer cardType_id);
    Page<Card> getByBankId(Integer bank_id, Pageable pageable);
    Page<Card> getByCardTypeId(Integer cardType_id, Pageable pageable);
    List<Card> getByClientId(UUID client_id);
    Optional<Card> findByUsername(String username);
//    boolean existsByUsernameAndCardTypeAndIdNot(String username, CardType cardType, UUID id);
}
