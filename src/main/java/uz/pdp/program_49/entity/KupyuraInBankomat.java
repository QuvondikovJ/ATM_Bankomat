package uz.pdp.program_49.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class KupyuraInBankomat {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

@OneToOne
private Kupyura kupyura;

private Integer count;

@OneToOne
private Bankomat bankomat;

@ManyToOne
private WithdrawMoney withdrawMoney;

}
