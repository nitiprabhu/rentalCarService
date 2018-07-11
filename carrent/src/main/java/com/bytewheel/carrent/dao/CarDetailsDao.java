package com.bytewheel.carrent.dao;

import com.bytewheel.carrent.model.CarDetails;
import com.bytewheel.carrent.repository.CarDetailsRepository;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CarDetailsDao {
  @Autowired
  private CarDetailsRepository carDetailsRepository;

  public List<String> getDistinctCategory() {
    return StreamSupport.stream(() -> carDetailsRepository.findAll().spliterator(),
                                (Spliterator.IMMUTABLE | Spliterator.NONNULL), false)
        .map(CarDetails::getCategory)
        .distinct()
        .collect(Collectors.toList());
  }

  public Stream<CarDetails> getCarDetailsByCategory(String category) {
    return carDetailsRepository.getCarDetailsByCategory(category);
  }

}
