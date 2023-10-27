package com.TruckBooking.ContractRateUpload.Dao;


import com.TruckBooking.TruckBooking.Entities.Load;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TruckBooking.ContractRateUpload.Entity.Indent;

import java.util.List;


@Repository
public interface IndentDao extends JpaRepository<Indent,Long>{
    public List<Indent> findByStatus(Load.Status status);

}