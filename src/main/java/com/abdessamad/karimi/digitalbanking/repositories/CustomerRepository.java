package com.abdessamad.karimi.digitalbanking.repositories;

import com.abdessamad.karimi.digitalbanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

}
