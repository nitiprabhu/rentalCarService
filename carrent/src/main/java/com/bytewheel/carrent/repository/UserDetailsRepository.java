package com.bytewheel.carrent.repository;


import com.bytewheel.carrent.model.UserDetails;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface UserDetailsRepository extends CassandraRepository<UserDetails> {

  @Query("select * from bytewheel.user_details where upin = ?0")
  public UserDetails getByUpin(String upin);

}
