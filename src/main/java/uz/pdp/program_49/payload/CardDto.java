package uz.pdp.program_49.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class CardDto {
private Integer bankId;
private String  clientId;
private String  password;
private Integer cardTypeId;
private Integer roleId;


}
