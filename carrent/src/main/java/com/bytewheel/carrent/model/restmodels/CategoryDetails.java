package com.bytewheel.carrent.model.restmodels;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CategoryDetails {
  List<String> categories;
  String requestedDate;
  String upin;
}
