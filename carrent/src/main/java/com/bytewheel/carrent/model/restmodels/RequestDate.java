package com.bytewheel.carrent.model.restmodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class RequestDate {
  String upin;
  String requestDate;
}
