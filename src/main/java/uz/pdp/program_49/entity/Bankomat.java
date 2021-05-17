package uz.pdp.program_49.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.pdp.program_49.entity.template.General;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Bankomat extends General {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CardType cardType;

    @ManyToOne
    private Bank bank;

    private Double maxWithdrawMoney; // maximum bankomatdan bir urunishda 1 million sum yoki 100 $ dan
    // ko'p yechish mumkin bo'lmasin

    private Double balance= 0.0; // minimum 20 mln yoki 2000$ qolsa ACCOUNTING_MANAGER ni emailiga xabar boradi

    private String city;

    private String district;

    private String street;

    @ManyToOne
    private Card card; // bu card ga commisiya larni puli tushuib boradi


}
