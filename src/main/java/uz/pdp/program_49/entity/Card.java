package uz.pdp.program_49.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.pdp.program_49.entity.Bank;
import uz.pdp.program_49.entity.Client;
import uz.pdp.program_49.entity.template.General;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Card extends General implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;

    private String username;  // 16 xonali takrorlanmas son

    @ManyToOne
    private Bank bank;

    private Integer cvvCode;

    @ManyToOne
    private Client client;

    private Timestamp expiryDate; // yangi card ochilgandan boshlab 4 yil amal qilish muddati bo'lsin

    private String password; // 4 xonali raqam

    @ManyToOne
    private CardType cardType;

    private Double balance;  // default holatda card yaratilganda, bu card HUMO yoki
    // UZCARD bo'lsa 500 sum balansi bo'lsin, VISA bo'lsa 1 dollar balansi bo'lsin

    private boolean active = true;

    @ManyToOne
    private Role role;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled = true;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
