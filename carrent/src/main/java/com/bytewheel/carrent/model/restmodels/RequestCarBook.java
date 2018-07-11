package com.bytewheel.carrent.model.restmodels;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class RequestCarBook {
  private String upin;
  private String requestDate;
  private List<CarBookedDetails> carBookedDetails;

  @AllArgsConstructor
  @Data
  @NoArgsConstructor
  public static class CarBookedDetails {
    String category;
    String carId;
    int numberOfCarBooked;
    boolean bookedSuccessful;
    double costPerDay;
  }
}

