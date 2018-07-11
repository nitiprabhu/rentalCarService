package com.bytewheel.carrent.model;

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
@Table(value = "car_details")
public class CarDetails {
  @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
  private String category;

  @PrimaryKeyColumn(name = "car_id", type = PrimaryKeyType.CLUSTERED)
  private String carId;

  @Column(value = "car_name")
  private String carName;

  @Column(value = "max_car_avail")
  private int maxCarAvail;

  @Column(value = "cost_per_day")
  private double costPerDay;


}
