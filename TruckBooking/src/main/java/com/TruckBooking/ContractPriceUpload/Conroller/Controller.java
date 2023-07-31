package com.TruckBooking.TruckBooking.Controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.TruckBooking.TruckBooking.Entities.Load;
import com.TruckBooking.TruckBooking.Model.LoadRequest;
import com.TruckBooking.TruckBooking.Service.LoadServiceImpl;

import ContractPriceUpload.ContractPriceUpload;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class LoadController {

	//upload the excel file.
    @PostMapping("/ContractRateUpload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (ContractRateService.checkExcelFormat(file)) {
            //true
            this.ContractRateService.save(file);

            return ResponseEntity.ok(Map.of("message", "File is uploaded and data is saved to database"));


        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file ");
    }

}
