package org.example.uber.entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "driver", indexes = { @Index(name = "idx_driver_vehicle_id", columnList = "vehicleId")})
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private Double rating;

    private String vehicleId;
    private Boolean available;

    @Column(columnDefinition = "Geometry(Point, 4326)")
    Point location;
}
