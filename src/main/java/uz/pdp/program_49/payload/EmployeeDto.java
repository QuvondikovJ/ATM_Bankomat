package uz.pdp.program_49.payload;

import lombok.Data;

@Data
public class EmployeeDto {
    private String  firstName;
    private String  lastName;
    private String  phoneNumber;
    private String  password;
    private String  username;
    private Integer roleId;

}
