package com.abdessamad.karimi.digitalbanking.web;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.abdessamad.karimi.digitalbanking.dtos.CustomerDTO;
import com.abdessamad.karimi.digitalbanking.entities.Customer;
import com.abdessamad.karimi.digitalbanking.exceptions.CustomerException;
import com.abdessamad.karimi.digitalbanking.services.BankAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountService bankAccountService;
    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<CustomerDTO> customer(){
        return bankAccountService.listCostumer();
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")

    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerException {
        return  bankAccountService.getCustomer(customerId);
    }
    @PostMapping("/customers")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")

    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return  bankAccountService.saveCustomer(customerDTO);
    }
    @PutMapping("/customers/{customerId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")

    public CustomerDTO updateCustomer(@PathVariable Long customerId ,@RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);

    }
    @DeleteMapping("/customers/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")

    public void deleteCustomer(@PathVariable Long id){
            bankAccountService.deleteCustomer(id);
    }
}
