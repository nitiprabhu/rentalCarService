package com.bytewheel.carrent.dao;

import com.bytewheel.carrent.model.UserDetails;
import com.bytewheel.carrent.repository.UserDetailsRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsDao {
  @Autowired
  private UserDetailsRepository userDetailsRepository;

  public Optional<UserDetails> getByUpin(String upin) {
    return Optional.ofNullable(userDetailsRepository.getByUpin(upin));
  }
}
