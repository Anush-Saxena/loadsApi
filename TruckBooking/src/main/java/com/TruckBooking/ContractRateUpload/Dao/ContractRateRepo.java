package com.TruckBooking.ContractRateUpload.Dao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.TruckBooking.ContractRateUpload.Entity.Rates;

@Repository
public interface ContractRateRepo extends JpaRepository<Rates,Long> {
// @Query("SELECT p FROM Product p WHERE p.station = :station AND p.weight = :weight ORDER BY p.rate")
    List<Rates> findByUnLoadingPointAndWeightOrderByRateAsc(@PathVariable("unLoadingPoint") String station,@PathVariable("weight") Integer weight);

} 