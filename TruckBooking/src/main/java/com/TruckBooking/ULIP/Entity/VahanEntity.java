package com.TruckBooking.ULIP.Entity;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VahanEntity {
    @NotBlank
    private String vehiclenumber;
}