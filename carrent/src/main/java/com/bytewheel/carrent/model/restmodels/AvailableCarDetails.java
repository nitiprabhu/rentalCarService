package com.bytewheel.carrent.model.restmodels;

import com.bytewheel.carrent.model.CarDetails;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class AvailableCarDetails {
  String requestDate;
  List<CarDetails> carDetails;
  String upin;
}
