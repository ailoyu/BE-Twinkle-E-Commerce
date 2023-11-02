package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    @Query(value = "SELECT * FROM provider p " +
            "WHERE " +
            "(:address IS NULL OR :address = '0' OR p.address LIKE %:address%) " +
            "AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%)", nativeQuery = true)
    Page<Provider> searchProviders(String keyword, String address, PageRequest pageRequest);
}
