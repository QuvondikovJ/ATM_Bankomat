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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"kupyura", "currency"}))
public class Kupyura extends General {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

private Integer kupyura; //1000, 5000, 10000, 50000, ....

    private String currency; // sum yoki dollar, ....





}