package com.bytewheel.carrent.model.restmodels;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class RequestCategories {
  private String upin;
  private String requestDate;
  private List<String> categoriesOpted;
}
