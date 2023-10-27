package com.TruckBooking.ContractRateUpload.Entity;

import com.TruckBooking.TruckBooking.Entities.Load;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.repository.cdi.Eager;

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
    private List<String> transporterIdList = new ArrayList<>(); // Initialize the list
    // TidList is the list for all transporter Id's that we had find rank for.

    @NonNull
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "Email")
    private List<String> transporterEmail = new ArrayList<>();
    // Email List of all the transporters, so we do not have to make connection to database again and again for address of each.

    @NotNull
    @Column(name = "Position")
    private int position;
    //Position is an index for the transporter to whom our system has assigned the load and waiting for the conformation.

    @UpdateTimestamp
    @Column(name = "AssignedTime")
    private Timestamp assignedTime;

    private Load.Status status;

    // Custom constructor
    public Indent(String loadId, List<String>transporter, int position, List<String> transporterEmail,Load.Status status) {
        this.loadId = loadId;
        this.transporterIdList = transporter;
        this.position=position;
        this.transporterEmail =transporterEmail;
        this.status = status;
    }
}
