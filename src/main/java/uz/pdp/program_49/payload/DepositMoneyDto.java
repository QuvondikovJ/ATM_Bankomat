package uz.pdp.program_49.payload;

import lombok.Data;

import java.util.List;

@Data
public class DepositMoneyDto {
private Integer bankomatId;
private List<Integer> kupyuraId;
private List<Integer> howMuchKupyura;


}
