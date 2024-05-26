package com.abdessamad.karimi.digitalbanking;

import com.abdessamad.karimi.digitalbanking.dtos.BankAccountDTO;
import com.abdessamad.karimi.digitalbanking.dtos.CurrentBankAccountDTO;
import com.abdessamad.karimi.digitalbanking.dtos.CustomerDTO;
import com.abdessamad.karimi.digitalbanking.dtos.SavingBankAccountDTO;
import com.abdessamad.karimi.digitalbanking.entities.*;
import com.abdessamad.karimi.digitalbanking.enums.AccountStatus;
import com.abdessamad.karimi.digitalbanking.enums.OpType;
import com.abdessamad.karimi.digitalbanking.exceptions.BalanceNotSufficientException;
import com.abdessamad.karimi.digitalbanking.exceptions.BankAccountNotFoundException;
import com.abdessamad.karimi.digitalbanking.exceptions.CustomerException;
import com.abdessamad.karimi.digitalbanking.repositories.BankAccountRepository;
import com.abdessamad.karimi.digitalbanking.repositories.CustomerRepository;
import com.abdessamad.karimi.digitalbanking.repositories.OperationRepository;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import             java.util.stream.Stream;

import com.abdessamad.karimi.digitalbanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("abdo","omar","khalid").forEach(name->{
                        CustomerDTO customerDTO = new CustomerDTO();
                        customerDTO.setName(name);
                        customerDTO.setEmail(name+"@gmail.com");
                    bankAccountService.saveCustomer(customerDTO);
                    });

            bankAccountService.listCostumer().forEach(cust ->{
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*9000,9000,cust.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*4000,5.5,cust.getId());

                } catch (CustomerException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccountList = bankAccountService.bankAccountList();
            for(BankAccountDTO bankAccount : bankAccountList){
                for (int i = 0; i <10 ; i++) {
                    String accountId;
                    if( bankAccount instanceof SavingBankAccountDTO){
                        accountId = ((SavingBankAccountDTO) bankAccount).getId();
                    }else {
                        accountId = ((CurrentBankAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId,1000+Math.random()*23000,"Creadit");
                    bankAccountService.debit(accountId,100+Math.random()*2300,"Debit");
                }

            }
        };
    }

    //@Bean

    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            OperationRepository operationRepository){
        return  args -> {

            Stream.of("abdo","omar","khalid").forEach(name->{
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);

            });
            customerRepository.findAll().forEach(cust ->{
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(5000);
                currentAccount.setCurrency("50000");
                bankAccountRepository.save(currentAccount);


                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());

                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(950);
                savingAccount.setCurrency("50000");
                bankAccountRepository.save(savingAccount);

            });
            bankAccountRepository.findAll().forEach(acc ->{
                for (int i = 0; i < 10; i++) {
                    Operation operation = new Operation();
                    operation.setAmount(5000);
                    operation.setType(Math.random()>0.5?OpType.DEBIT:OpType.CREDIT);
                    operation.setDate(new Date());
                    operation.setBankAccount(acc);
                    operationRepository.save(operation);
                }
            });

        };
    }

}
