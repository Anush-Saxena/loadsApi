package com.TruckBooking.Invoice_Services.Services;

import com.TruckBooking.Invoice_Services.Entity.Invoice;
import com.TruckBooking.Invoice_Services.Model.InvoiceRequest;
import com.TruckBooking.Invoice_Services.Response.CreateInvoiceResponse;
import com.TruckBooking.Invoice_Services.Response.UpdateInvoiceResponse;

import java.util.List;

public interface InvoiceService {
    public CreateInvoiceResponse addInvoice(InvoiceRequest invoiceModel);
    public List<Invoice>getInvoices(String transporterId);
    public UpdateInvoiceResponse updateInvoice(String invoiceId, InvoiceRequest invoiceModel);
    public CreateInvoiceResponse getInvoiceById(String invoiceId);
  public   CreateInvoiceResponse getInvoicebyshipperId(String shipperId);

  }