package com.abdessamad.karimi.digitalbanking.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.abdessamad.karimi.digitalbanking.dtos.*;
import com.abdessamad.karimi.digitalbanking.entities.*;
import com.abdessamad.karimi.digitalbanking.enums.OpType;
import com.abdessamad.karimi.digitalbanking.exceptions.BalanceNotSufficientException;
import com.abdessamad.karimi.digitalbanking.exceptions.BankAccountNotFoundException;
import com.abdessamad.karimi.digitalbanking.exceptions.CustomerException;
import com.abdessamad.karimi.digitalbanking.mappers.BankAccountMapperImpl;
import com.abdessamad.karimi.digitalbanking.repositories.BankAccountRepository;
import com.abdessamad.karimi.digitalbanking.repositories.CustomerRepository;
import com.abdessamad.karimi.digitalbanking.repositories.OperationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j // pour la journalisation
public class BankAccountServiceImpl implements BankAccountService{
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private OperationRepository operationRepository;
    private BankAccountMapperImpl dtoMapper;
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTOomer) {
        log.info("saving new Customer ");
        Customer customer =  dtoMapper.fromCustomer(customerDTOomer);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerException("Customer not found");

        CurrentAccount bankAccount= new CurrentAccount();

        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCustomer(customer);
        bankAccount.setOverDraft(overDraft);
        CurrentAccount savedbankAcount = bankAccountRepository.save(bankAccount);

        return dtoMapper.fromCurrentBankAccount(savedbankAcount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double inerestRate, Long customerId) throws CustomerException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerException("Customer not found");

        SavingAccount bankAccount= new SavingAccount();

        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCustomer(customer);
        bankAccount.setInterestRate(inerestRate);
        SavingAccount savedSavingAccount = bankAccountRepository.save(bankAccount);

        return dtoMapper.fromSavingBankAccount(savedSavingAccount);


    }


    @Override
    public List<CustomerDTO> listCostumer() {
     List<Customer> customers =   customerRepository.findAll();
     List<CustomerDTO> customerDTOS =   customers.stream().map(cust->dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
        //collect pour le transmettre en liste
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount  = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double montant, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));        if(bankAccount.getBalance()< montant)
            throw  new BalanceNotSufficientException("Balance not sufficien");

        Operation operation = new Operation();
        operation.setType(OpType.DEBIT);
        operation.setAmount(montant);
        operation.setDescription(description);
        operation.setDate(new Date());
        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance()-montant);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void credit(String accountId, double montant, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        Operation operation = new Operation();
        operation.setType(OpType.CREDIT);
        operation.setAmount(montant);
        operation.setDescription(description);
        operation.setDate(new Date());
        operation.setBankAccount(bankAccount);
        operationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance()+montant);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double montant) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.debit(accountIdSource,montant,"Transfer to "+accountIdDestination);
        this.credit(accountIdDestination,montant,"Transfer from "+accountIdSource);
    }
    @Override
    public List<BankAccountDTO> bankAccountList(){
         List<BankAccount> bankAccountList = bankAccountRepository.findAll();
            List<BankAccountDTO> bankAccountDTOS = bankAccountList.stream().map(account-> {
          if( account instanceof SavingAccount){
              SavingAccount savingAccount = (SavingAccount) account;
              return dtoMapper.fromSavingBankAccount(savingAccount);
          }else {
              CurrentAccount currentAccount = (CurrentAccount) account;
              return dtoMapper.fromCurrentBankAccount(currentAccount);

          }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }
    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerException {
         Customer customer = customerRepository.findById(customerId).orElseThrow(()-> new CustomerException(" not found"));
        return dtoMapper.fromCustomer(customer);
    }
    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("saving new Customer ");
        Customer customer =  dtoMapper.fromCustomer(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }
    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);

    }
    @Override
    public List<OperationDTO> accountHistory(String accountId){
        List<Operation> operationDTOS = operationRepository.findByBankAccountId(accountId);
        return operationDTOS.stream().map(operation -> dtoMapper.fromOperation(operation)).collect(Collectors.toList());

    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount==null){
            throw  new BankAccountNotFoundException("not found bank account");
        }


        Page<Operation> operationPage = operationRepository.findByBankAccountId(accountId, PageRequest.of(page,size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<OperationDTO> operationDTOS = operationPage.getContent().stream().map(opp->dtoMapper.fromOperation(opp)).collect(Collectors.toList());
        accountHistoryDTO.setOperationDTOS(operationDTOS);
        accountHistoryDTO.setAccoutnId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setCurrentPage(page);

        accountHistoryDTO.setTotalPages(operationPage.getTotalPages());
        return accountHistoryDTO;
    }


}
