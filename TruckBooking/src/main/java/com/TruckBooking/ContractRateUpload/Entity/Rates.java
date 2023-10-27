package com.TruckBooking.ContractRateUpload.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name="Contract_Rates")
public class Rates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Loading Point Cannot Be Empty")
    @Column(name = "loadingPointCity")
    private String loadingPointCity;

    @NotBlank(message = "UnLoading Point Cannot Be Empty")
    @Column(name = "unloadingPointCity")
    private String unloadingPointCity;

    @NotNull(message = "Weight Cannot Be Empty")
    @Column(name = "weight")
    private String weight;

    @Column(name = "rate")
    private Integer rate;

    @Column(name = "TransporterId")
    private String TransporterId;

    @Column(name = "TransporterName")
    private String TransporterName;

    @NotBlank(message = "Transporter Email Cannot Be Empty")
    @Column(name="TransporterEmail")
    private String TransporterEmail;
}