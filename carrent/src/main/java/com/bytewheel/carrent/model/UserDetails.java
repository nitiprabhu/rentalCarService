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
@Table(value = "user_details")
public class UserDetails {
  @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
  private String upin;
  @Column
  private String email;
  @Column
  private String location;
  @Column
  private String phoneNumber;
}
