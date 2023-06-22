package com.TruckBooking.TruckBooking.Response;

import com.TruckBooking.TruckBooking.Entities.Load;
import lombok.Data;

import java.util.List;

@Data
public class TransporterResponse {
    private List<Load> loadList;
    public TransporterResponse(List<Load> loadList){
        this.loadList=loadList;
    }
}
