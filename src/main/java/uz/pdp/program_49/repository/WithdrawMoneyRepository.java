package uz.pdp.program_49.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.program_49.entity.WithdrawMoney;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface WithdrawMoneyRepository extends JpaRepository<WithdrawMoney, UUID> {

    @Query(value = "select * from withdraw_money as w " +
            " where w.created_at>:timestamp1 and w.created_at<:timestamp2 " +
            " and w.withdraw_money_or_deposit_money=:active and w.bankomat_id=:bankomatId", nativeQuery = true)
    List<WithdrawMoney> getByWithdrawMoney(Timestamp timestamp1, Timestamp timestamp2, boolean active, Integer bankomatId);

    //
//
    @Query(value = "select * from withdraw_money as w " +
            " where w.created_at>:timestamp1 and w.created_at<:timestamp2 " +
            " and w.withdraw_money_or_deposit_money=:active and w.bankomat_id=:bankomatId", nativeQuery = true)
    List<WithdrawMoney> getByDepositMoney(Timestamp timestamp1, Timestamp timestamp2, boolean active, Integer bankomatId);

    @Query(value = "select count(*) > 0 from withdraw_money as w where w.created_at>:timestamp1 and w.created_at<:timestamp2 " +
            " and w.withdraw_money_or_deposit_money=:active and w.bankomatId=:bankomatId", nativeQuery = true)
    boolean existsByDateAndBankomatId(Timestamp timestamp1, Timestamp timestamp2, boolean active, Integer bankomatId);



}
