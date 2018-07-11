package com.bytewheel.carrent.controller;

import com.bytewheel.carrent.model.restmodels.CategoryDetails;
import com.bytewheel.carrent.model.restmodels.RequestCarBook;
import com.bytewheel.carrent.model.restmodels.RequestCategories;
import com.bytewheel.carrent.model.restmodels.RequestDate;
import com.bytewheel.carrent.service.CarAvailabilityTracker;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BytewheelController {
  @Autowired
  private CarAvailabilityTracker carAvailabilityTracker;

  /**
   * This controller which accepts the date picked by the user, and show relevant categories
   *
   * @param requestDate date picked by user for the rent
   * @return overall categories available on our portal
   */
  @PostMapping(value = "/pickupdate")
  public CategoryDetails getVehiclesListAvailableAsOfRequestedDate(
      @RequestBody RequestDate requestDate) {
    return carAvailabilityTracker.getListOfUniqueCategories(requestDate);
  }

  /**
   * Method to get a list of car details available as on date picked by user.
   *
   * @param requestCategories list of categories opted by the user
   * @return list of car details with cost, car details with number of cars available
   */
  @PostMapping(value = "/optedCategories")
  public ResponseEntity<?> getAvailableCarDetailsAsOfDate(
      @RequestBody RequestCategories requestCategories) {

    if (null == requestCategories || null == requestCategories.getCategoriesOpted()) {
      return ResponseEntity.badRequest().body("Invalid data");
    }

    return ResponseEntity.ok(carAvailabilityTracker.getAvailableCarDetails(requestCategories));

  }

  /**
   * Controller to book the car if available as on the day.
   *
   * @param requestCarBook list of cars book request given by the user
   * @return booked the car if available with a status true; false otherwise
   */
  @PostMapping(value = "/bookcar")
  public ResponseEntity<?> bookCarForTheDay(@RequestBody RequestCarBook requestCarBook) {
    if (null == requestCarBook) {
      return ResponseEntity.badRequest().body("CarBooking request received null or invalid");
    }
    return ResponseEntity.ok(carAvailabilityTracker.bookCarForRent(requestCarBook));
  }

  /**
   * Controller to generate a report for the transaction done on the day.
   *
   * @param requestDate requestDate given by the admin team to get report
   * @return reports data of the transaction
   */
  @PostMapping(value = "/collectreport")
  public ResponseEntity<?> collectReportForTheDay(@RequestParam String requestDate) {

    if (Strings.isNullOrEmpty(requestDate)) {
      return ResponseEntity.badRequest().body("Invalid date");
    }
    return ResponseEntity.ok(carAvailabilityTracker.collectDataForReport(requestDate));
  }

}
