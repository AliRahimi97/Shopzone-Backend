package com.example.onlineshopback.dto.response;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class AdminResponseDTO {
    

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    
}
