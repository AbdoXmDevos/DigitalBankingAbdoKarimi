package com.abdessamad.karimi.digitalbanking.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.abdessamad.karimi.digitalbanking.entities.BankAccount;
import com.abdessamad.karimi.digitalbanking.enums.OpType;

import java.util.Date;

@Data

public class OperationDTO {

    private Long id;
    private Date date;
    private double amount;
    private OpType type;
    private String description;
}
