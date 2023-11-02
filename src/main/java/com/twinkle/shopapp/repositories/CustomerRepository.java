package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findCustomerByUserId(long userId);

}
