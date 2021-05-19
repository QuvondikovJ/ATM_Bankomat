package uz.pdp.program_49.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class BankomatDto {
private Integer     cardTypeId;
private Integer     bankId;
private String      cardId;
    private String  street;
    private Integer homeNumber;
    private Integer districtId;



}
