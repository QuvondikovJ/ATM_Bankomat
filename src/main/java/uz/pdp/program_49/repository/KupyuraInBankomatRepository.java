package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.program_49.entity.Kupyura;
import uz.pdp.program_49.entity.KupyuraInBankomat;

import java.util.List;
import java.util.Optional;

public interface KupyuraInBankomatRepository extends JpaRepository<KupyuraInBankomat, Integer > {
    Optional<KupyuraInBankomat> findByKupyuraIdAndBankomatId(Integer kupyura_id, Integer bankomat_id);
List<KupyuraInBankomat> getByBankomatId(Integer bankomat_id);
    boolean existsByBankomatId(Integer bankomat_id);

    @Transactional
    @Modifying
    @Query(value = "delete from kupyura_in_bankomat as k where k.bankomat_id=:bankomatId",nativeQuery = true)
    void  deleteByBankomatId(Integer bankomatId);

}
