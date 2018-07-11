package com.bytewheel.carrent.repository;

import com.bytewheel.carrent.model.UserTracking;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface UserTrackingRepository extends CassandraRepository<UserTracking> {

  @Query(" select * from bytewheel.user_tracking where upin = ?0")
  public Stream<UserTracking> getByUpin(String upin);

  @Query(" select * from bytewheel.user_tracking where upin = ?0 and date = ?1")
  public Stream<UserTracking> getByUpinAndDate(String upin, LocalDate localDate);

  @Query(" select * from bytewheel.user_tracking where date = ?0")
  public Stream<UserTracking> getByDate(String date);
}
