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
public class Address extends General {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

@Column(nullable = false)
private String street;

@Column(nullable = false)
    private Integer homeNumber;

@ManyToOne
    private District district;

}
