package com.bytewheel.carrent.service;

import com.bytewheel.carrent.dao.CarDetailsDao;
import com.bytewheel.carrent.dao.UserDetailsDao;
import com.bytewheel.carrent.model.CarDetails;
import com.bytewheel.carrent.model.UserDetails;
import com.bytewheel.carrent.model.UserTracking;
import com.bytewheel.carrent.model.restmodels.*;
import com.bytewheel.carrent.repository.UserTrackingRepository;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CarAvailabilityTracker {
  private static final Integer DEFAULT_MAX_CARS_AVAILABLE = 2;
  private static final String STATUS_SUCCESS = "SUCCESS";
  //hardcoded it for now, best practise is to take from db itself
  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private CarDetailsDao carDetailsDao;
  @Autowired
  private UserTrackingRepository userTrackingRepository;
  @Autowired
  private EmailService emailService;
  @Autowired
  private UserDetailsDao userDetailsDao;

  /**
   * Method to collect data for reports.
   *
   * @param requestDate requestDate given by the admin to generate a report
   * @return report of the transaction
   */
  public List<UserTracking> collectDataForReport(String requestDate) {
    return userTrackingRepository.getByDate(requestDate)
        .collect(Collectors.toList());
  }

  /**
   * Method to book a car for the rent.
   *
   * @param requestCarBook requestCarBook details
   * @return book car if available with a status true; false otherwise
   */
  public RequestCarBook bookCarForRent(RequestCarBook requestCarBook) {
    LocalDate requestDate = LocalDate.parse(requestCarBook.getRequestDate());

    requestCarBook
        .getCarBookedDetails()
        .forEach(carBookedDetails -> {
          boolean isCarBooked = bookCarForTheDay(requestDate,
                                                 carBookedDetails.getCarId(),
                                                 carBookedDetails.getNumberOfCarBooked());
          carBookedDetails.setBookedSuccessful(isCarBooked);

          if (isCarBooked) {
            String upin = requestCarBook.getUpin();
            updateUserTracking(upin, requestDate, carBookedDetails);
            updateBookedStatusToUser(upin, requestDate, carBookedDetails);
          }
        });

    return requestCarBook;


  }

  /**
   * Method to check whether car is booked or not.
   *
   * @param requestedBookingDate booking request date
   * @param carId                carId of the book
   * @param numberOfCarsBooked   number of cars booked by the user
   * @return true if booked
   */
  private boolean bookCarForTheDay(LocalDate requestedBookingDate,
                                   String carId,
                                   double numberOfCarsBooked) {

    double currentCardIdCount = getBookingDetailsCountByCarId(requestedBookingDate, carId);

    if (DEFAULT_MAX_CARS_AVAILABLE < currentCardIdCount) {
      log.info("No cars {} available for the day {}. Booking is not successful",
               carId, requestedBookingDate);
      return false;
    }

    if (currentCardIdCount + numberOfCarsBooked > DEFAULT_MAX_CARS_AVAILABLE) {
      log.warn("Number of booking request exceeds max available count. Booking is not successful");
      return false;
    }

    redisTemplate.opsForZSet()
        .incrementScore(requestedBookingDate.toString(), carId, numberOfCarsBooked);
    log.info("Booking is succesfull for car-id {}, booking date {}", carId, requestedBookingDate);
    return true;
  }

  /**
   * Method to update booked status in the db
   *
   * @param upin             upin of the user
   * @param requestDate      requestDate booked by the user
   * @param carBookedDetails carBookedDetails
   */
  private void updateUserTracking(String upin,
                                  LocalDate requestDate,
                                  RequestCarBook.CarBookedDetails carBookedDetails) {
    double totalCost = getTotalCost(carBookedDetails.getNumberOfCarBooked(),
                                    carBookedDetails.getCostPerDay());

    UserTracking userTracking = UserTracking.builder()
        .upin(upin)
        .date(requestDate)
        .lastUpdated(Instant.now())
        .numberOfCars(carBookedDetails.getNumberOfCarBooked())
        .carId(carBookedDetails.getCarId())
        .totalCost(totalCost)
        .status(STATUS_SUCCESS)
        .build();
    userTrackingRepository.insert(userTracking);
  }

  private void updateBookedStatusToUser(String upin,
                                        LocalDate requestDate,
                                        RequestCarBook.CarBookedDetails carBookedDetails) {

    Optional<UserDetails> userDetailsOptional = userDetailsDao.getByUpin(upin);
    if (!userDetailsOptional.isPresent()) {
      log.error("User upin {} information is not available", upin);
      return;
    }

    double totalCost = getTotalCost(carBookedDetails.getNumberOfCarBooked(),
                                    carBookedDetails.getCostPerDay());

    UserDetails userDetails = userDetailsOptional.get();
    String subject = MessageFormat.format("Booking Status - {0}", requestDate);
    String actualMessage = MessageFormat.format(
        "Successfuly booked your car for rent on {0}. Total cost is ${1}",
        requestDate,
        totalCost);
    emailService.sendSimpleMessage(userDetails.getEmail(), subject, actualMessage);
  }

  /**
   * Method to get a current booking count of the car as on the date requested
   *
   * @param requestedBookingDate requestBookedDate by the user
   * @param carId                carId of the car requested by the user
   * @return number of cars booked already
   */
  private double getBookingDetailsCountByCarId(LocalDate requestedBookingDate, String carId) {

    return Optional.ofNullable(redisTemplate.opsForZSet()
                                   .score(requestedBookingDate.toString(), carId))
        .orElse(0.0);
  }

  /**
   * Method to get a total cost based on price and quantity
   *
   * @param numberOfCarBooked number of cars booked by the user
   * @param costPerDay        cost per car
   * @return total cost of the car
   */
  private double getTotalCost(int numberOfCarBooked, double costPerDay) {
    return numberOfCarBooked * costPerDay;
  }

  public CategoryDetails getListOfUniqueCategories(RequestDate requestDate) {
    List<String> categories = carDetailsDao.getDistinctCategory();
    return new CategoryDetails(categories, requestDate.getRequestDate(), requestDate.getUpin());
  }

  /**
   * Method to get a availableCarDetails as on the day
   *
   * @param requestCategories requestCategories
   * @return availableCarDetails
   */
  public AvailableCarDetails getAvailableCarDetails(RequestCategories requestCategories) {
    String requestDate = requestCategories.getRequestDate();

    List<CarDetails> carDetailsAsOfDate = requestCategories.getCategoriesOpted()
        .stream()
        .flatMap(category -> carDetailsDao.getCarDetailsByCategory(category))
        .peek(carDetails -> {
          int currentCount = new Double(getBookingDetailsCountByCarId(LocalDate.parse(
              requestDate),
                                                                      carDetails.getCarId()))
              .intValue();
          int availableCount = carDetails.getMaxCarAvail() - currentCount;
          carDetails.setMaxCarAvail(availableCount);
        })
        .collect(Collectors.toList());

    return new AvailableCarDetails(requestDate, carDetailsAsOfDate, requestCategories.getUpin());
  }

  /**
   * This service method could be used when book is cancelled. Then we will avail those cars/vehicles for other users
   *
   * @param requestedBookingDate requestDate picked by the user
   * @param carId                carId of the car
   * @param numberOfCarsBooked   numberOfCarsBooked by the user
   * @return true if carBookingCancelled
   */
  public boolean cancelCarForTheDay(LocalDate requestedBookingDate,
                                    String carId,
                                    double numberOfCarsBooked) {

    double currentCardIdCount = getBookingDetailsCountByCarId(requestedBookingDate, carId);
    double decrementValue = -1 * numberOfCarsBooked;

    if (decrementValue + currentCardIdCount < 0.0) {
      log.warn("Invalid booking request recieved. UnBooking is not successful");
      return false;
    }

    redisTemplate.opsForZSet()
        .incrementScore(requestedBookingDate.toString(), carId, decrementValue);
    return true;
  }
}
