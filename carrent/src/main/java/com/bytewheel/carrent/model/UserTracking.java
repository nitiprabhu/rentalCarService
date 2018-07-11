package com.bytewheel.carrent.model;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(value = "user_tracking")
public class UserTracking {
  @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
  private String upin;

  @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
  private LocalDate date;

  @PrimaryKeyColumn(name = "car_id", type = PrimaryKeyType.CLUSTERED)
  private String carId;

  @PrimaryKeyColumn(name = "last_updated", type = PrimaryKeyType.CLUSTERED)
  private Instant lastUpdated;

  @Column(value = "number_of_cars")
  private int numberOfCars;

  @Column
  private String status;

  @Column(value = "total_cost")
  private double totalCost;
}
