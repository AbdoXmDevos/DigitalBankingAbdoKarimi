package com.abdessamad.karimi.digitalbanking.dtos;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.abdessamad.karimi.digitalbanking.entities.BankAccount;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;

}
