package com.health.care_management.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.health.care_management.Entity.OtpManager;

@Repository
public interface OtpRepository  extends JpaRepository<OtpManager,Long>{
    OtpManager findByUsername(String username);
}
