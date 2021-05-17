package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.program_49.entity.CardType;

import java.util.Optional;

public interface CardTypeRepository extends JpaRepository<CardType, Integer> {
}
