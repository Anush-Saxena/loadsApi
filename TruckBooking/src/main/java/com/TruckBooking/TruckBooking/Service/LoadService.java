package com.TruckBooking.TruckBooking.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.TruckBooking.TruckBooking.Entities.Load;
import com.TruckBooking.TruckBooking.Model.LoadRequest;
import com.TruckBooking.TruckBooking.Response.CreateLoadResponse;
import com.TruckBooking.TruckBooking.Response.UpdateLoadResponse;

public interface LoadService {
	public CreateLoadResponse addLoad(LoadRequest load);

	public List<Load> getLoads(Integer pageNo, String loadingPointCity, String unloadingPointCity, String shipperId,
			String truckType, boolean suggestedLoads, String transporterId, Timestamp startTimestamp, Timestamp endTimestamp);
	
	
	public UpdateLoadResponse updateLoad(String loadId, LoadRequest loadrequest);

	public CreateLoadResponse getLoad(String loadId);

	public void deleteLoad(String loadId);

}
