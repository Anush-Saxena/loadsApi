package com.TruckBooking.ULIP.Entity;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastagEntity {
    @NotBlank
    private String vehiclenumber;
}
