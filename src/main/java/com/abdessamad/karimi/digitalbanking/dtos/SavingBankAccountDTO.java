package com.abdessamad.karimi.digitalbanking.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.abdessamad.karimi.digitalbanking.entities.Customer;
import com.abdessamad.karimi.digitalbanking.entities.Operation;
import com.abdessamad.karimi.digitalbanking.enums.AccountStatus;

import java.util.Date;
import java.util.List;


@Data


public  class SavingBankAccountDTO extends  BankAccountDTO{
    private String id;
    private Date createdAt;
    private double balance;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double interestRate;
}
