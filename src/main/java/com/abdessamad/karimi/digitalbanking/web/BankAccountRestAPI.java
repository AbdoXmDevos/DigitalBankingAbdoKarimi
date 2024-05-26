package com.abdessamad.karimi.digitalbanking.web;

import lombok.AllArgsConstructor;
import com.abdessamad.karimi.digitalbanking.dtos.*;
import com.abdessamad.karimi.digitalbanking.exceptions.BalanceNotSufficientException;
import com.abdessamad.karimi.digitalbanking.exceptions.BankAccountNotFoundException;
import com.abdessamad.karimi.digitalbanking.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccont(@PathVariable String accountId) throws BankAccountNotFoundException {
        BankAccountDTO bankAccountDTO = bankAccountService.getBankAccount(accountId);
        return  bankAccountDTO;
    }
    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccount(){
        return bankAccountService.bankAccountList();

    }
    @GetMapping("/accounts/{accountId}/operations")
    public List<OperationDTO> history(@PathVariable String accountId){
        return bankAccountService.accountHistory(accountId);

    }
    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(@PathVariable String accountId,
                                               @RequestParam(name = "page",defaultValue = "0") int page ,
                                               @RequestParam(name = "size",defaultValue = "5") int size ) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page,size);

    }
    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;
    }
    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;
    }
    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfer(
                transferRequestDTO.getAccountSource(),
                transferRequestDTO.getAccountDestination(),
                transferRequestDTO.getAmount());
    }
}
