package uz.pdp.program_49.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.pdp.program_49.entity.template.General;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class WithdrawMoney extends General {
@Id
    @GeneratedValue
    private UUID id;

@OneToOne
private Card card;

private Integer withdrawMoney;

private boolean withdrawMoneyOrDepositMoney;

@ManyToOne
private Bankomat bankomat;

    public WithdrawMoney(boolean withdrawMoneyOrDepositMoney) {
        this.withdrawMoneyOrDepositMoney = withdrawMoneyOrDepositMoney;
    }
}
