package uz.pdp.program_49.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.program_49.entity.Kupyura;
import uz.pdp.program_49.entity.KupyuraInBankomat;

import java.util.List;
import java.util.Optional;

public interface KupyuraInBankomatRepository extends JpaRepository<KupyuraInBankomat, Integer> {
    Optional<KupyuraInBankomat> findByKupyuraIdAndBankomatIdAndActive(Integer kupyura_id, Integer bankomat_id, boolean active);

    List<KupyuraInBankomat> getByBankomatIdAndActive(Integer bankomat_id, boolean active);

    boolean existsByBankomatIdAndActive(Integer bankomat_id, boolean active);

    Page<KupyuraInBankomat> getByActive(boolean active, Pageable pageable);

    Optional<KupyuraInBankomat> findByIdAndActive(Integer id, boolean active);


    @Transactional
    @Modifying
    @Query(value = "update kupyura_in_bankomat as k set k.active=:active where k.bankomat_id=:bankomatId", nativeQuery = true)
    void editByBankomatId(Integer bankomatId, boolean active);

}
