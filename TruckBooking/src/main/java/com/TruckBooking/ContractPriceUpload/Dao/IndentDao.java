package com.TruckBooking.TruckBooking.Dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TruckBooking.TruckBooking.Entities.Rank;

@Repository
public interface IndentDao extends JpaRepository<Indent,Long>{
    
}
