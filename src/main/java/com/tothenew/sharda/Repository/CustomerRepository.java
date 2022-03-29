package com.tothenew.sharda.Repository;

import com.tothenew.sharda.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}