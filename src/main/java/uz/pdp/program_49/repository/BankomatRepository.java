package uz.pdp.program_49.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.program_49.entity.Bank;
import uz.pdp.program_49.entity.Bankomat;
import uz.pdp.program_49.entity.CardType;

import java.util.UUID;

public interface BankomatRepository extends JpaRepository<Bankomat, Integer> {
    boolean existsByStreetAndCardTypeAndBank(String street, CardType cardType, Bank bank);
    boolean existsByStreetAndCardTypeIdAndBankIdAndIdNot(String street, Integer cardType_id, Integer bank_id, Integer id);
    Page<Bankomat> getByBankId(Integer bank_id, Pageable pageable);
    Page<Bankomat> getByCardTypeId(Integer cardType_id, Pageable pageable);
    Page<Bankomat> getByCardId(UUID card_id, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update bankomat as b set b._max_withdraw_money=:maxWithdrawMoney where b.card_type_id=:cardTypeId",nativeQuery = true)
    void editMaxWithdrawMoneyByCardTypeId(Double maxWithdrawMoney, Integer cardTypeId);


}
