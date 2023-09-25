package com.TruckBooking.ULIP.Entity;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SarathiEntity {
    @NotBlank
    private String dlnumber;
    @NotBlank
    private String dob;
}
