package com.bytewheel.carrent.repository;

import com.bytewheel.carrent.model.CarDetails;
import java.util.stream.Stream;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface CarDetailsRepository extends CassandraRepository<CarDetails> {

  @Query("select * from bytewheel.car_details where category = ?0")
  Stream<CarDetails> getCarDetailsByCategory(String category);

  @Query("select DISTINCT category from bytewheel.car_details")
  Stream<String> getAllCategories();
}
