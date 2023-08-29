package com.TruckBooking.ContractRateUpload.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

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
    private List<String> transporterId = new ArrayList<>(); // Initialize the list

    @NonNull
    @Column(name = "Transporter")
    private String transporter;

    @NonNull
    @Column(name = "Email")
    private String email;

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
    public Indent(String loadId, List<String>transport,String transporter, String Email,TransporterStatus status) {
        this.loadId = loadId;
        this.transporterId = transport;
        this.transporter=transporter;
        this.email=Email;
        this.transporterStatus = status;
    }
}
