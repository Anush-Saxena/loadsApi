package com.TruckBooking.TruckBooking.Service;

import java.util.ArrayList;
import java.util.List;

import com.TruckBooking.TruckBooking.Entities.Load;
import com.TruckBooking.TruckBooking.Model.LoadRequest;
import com.TruckBooking.TruckBooking.Response.CreateLoadResponse;
import com.TruckBooking.TruckBooking.Response.UpdateLoadResponse;

public interface LoadService {
	public CreateLoadResponse addLoad(LoadRequest load);

	public List<Load> getLoads(Integer pageNo, String loadingPointCity, String unloadingPointCity, String shipperId,
			String truckType, String date, boolean suggestedLoads);

	public UpdateLoadResponse updateLoad(String loadId, LoadRequest loadrequest);

	public Load getLoad(String loadId);

	public void deleteLoad(String loadId);

    List<Load> getLoadsByTransporterId(String transporterId);
}
