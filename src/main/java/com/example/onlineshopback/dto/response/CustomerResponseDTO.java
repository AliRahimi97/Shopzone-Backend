package com.example.onlineshopback.dto.response;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class CustomerResponseDTO {

    
    private Integer id;

    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private LocalDateTime createdOn;

}
