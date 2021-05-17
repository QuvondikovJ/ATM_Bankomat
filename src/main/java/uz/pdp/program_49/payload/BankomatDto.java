package uz.pdp.program_49.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class BankomatDto {
private Integer   cardTypeId;
private Integer   bankId;
private String    city;
private String    district;
private String    street;
private String    cardId;



}
