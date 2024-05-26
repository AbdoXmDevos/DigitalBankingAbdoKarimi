package com.abdessamad.karimi.digitalbanking.repositories;

import com.abdessamad.karimi.digitalbanking.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {

}
