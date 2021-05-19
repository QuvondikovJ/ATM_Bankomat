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
    boolean existsByCardTypeAndBankAndAddress_HomeNumber(CardType cardType, Bank bank, Integer address_homeNumber);

    boolean existsByCardTypeIdAndBankIdAndIdNotAndAddress_HomeNumber(Integer cardType_id, Integer bank_id, Integer id, Integer address_homeNumber);

    Page<Bankomat> getByBankIdAndActive(Integer bank_id, boolean active, Pageable pageable);

    Page<Bankomat> getByCardTypeIdAndActive(Integer cardType_id, boolean active, Pageable pageable);

    Page<Bankomat> getByCardIdAndActive(UUID card_id, boolean active, Pageable pageable);

    boolean existsByIdAndActive(Integer id, boolean active);

    Bankomat getByIdAndActive(Integer id, boolean active);

    Page<Bankomat> getByActive(boolean active, Pageable pageable);


    @Transactional
    @Modifying
    @Query(value = "update bankomat as b set b.max_withdraw_money=:maxWithdrawMoney where b.card_type_id=:cardTypeId", nativeQuery = true)
    void editMaxWithdrawMoneyByCardTypeId(Double maxWithdrawMoney, Integer cardTypeId);


}
