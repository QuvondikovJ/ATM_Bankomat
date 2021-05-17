package uz.pdp.program_49.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.program_49.entity.enums.CardName;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CardType {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

@Enumerated(EnumType.STRING)
private CardName cardName;



}
