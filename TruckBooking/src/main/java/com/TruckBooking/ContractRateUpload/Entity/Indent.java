package com.TruckBooking.ContractRateUpload.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Indent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "loadId")
    private String loadId;

    @ElementCollection
    @CollectionTable(name = "transporter_ids", joinColumns = @JoinColumn(name = "indent_id"))
    @Column(name = "transporter_id")
    private List<String> TidList = new ArrayList<>(); // Initialize the list
    // TidList is the list for all transporter Id's that we had find rank for.

    @NonNull
    @ElementCollection
    @Column(name = "Email")
    private List<String> transporterEmail = new ArrayList<>();
    // Email List of all the transporters, so we do not have to make connection to database again and again for email of each.

    @NotNull
    @Column(name = "Position")
    private int position;
    //Position is an index for the transporter to whom our system has assigned the load and waiting for the conformation.

    @UpdateTimestamp
    @Column(name = "AssignedTime")
    private Timestamp assignedTime;

    @Enumerated(EnumType.STRING)
    @NonNull
    private TransporterStatus transporterStatus;

    public enum TransporterStatus {
        PENDING, ON_GOING, COMPLETED, EXPIRED, REJECTED
    }

    // Custom constructor
    public Indent(String loadId, List<String>transport, int position, List<String> transporterEmail,TransporterStatus status) {
        this.loadId = loadId;
        this.TidList = transport;
        this.position=position;
        this.transporterEmail =transporterEmail;
        this.transporterStatus = status;
    }
}
