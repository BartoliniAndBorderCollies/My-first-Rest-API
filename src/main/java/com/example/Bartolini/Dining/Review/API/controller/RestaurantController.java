package com.example.Bartolini.Dining.Review.API.controller;

import com.example.Bartolini.Dining.Review.API.model.Restaurant;
import com.example.Bartolini.Dining.Review.API.model.ReviewStatus;
import com.example.Bartolini.Dining.Review.API.model.User;
import com.example.Bartolini.Dining.Review.API.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RequestMapping("/restaurants")
@RestController
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;
    private final Pattern zipCodePattern = Pattern.compile("\\d{2}-\\d{3}");


    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) {
        validateNewRestaurant(restaurant);
        return restaurantRepository.save(restaurant);
    }

    private void validateNewRestaurant(Restaurant restaurant) {
        if (ObjectUtils.isEmpty(restaurant.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        validateZipCode(restaurant.getZipCode());

        Optional<Restaurant> existingRestaurant = restaurantRepository.findRestaurantsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode());
        if (existingRestaurant.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private void validateZipCode(String zipcode) {
        if (!zipCodePattern.matcher(zipcode).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    private Restaurant getRestaurant(@PathVariable Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isPresent()) {
            return restaurant.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    private void updateRestaurant(@PathVariable Long id, @RequestBody Restaurant updatedRestaurant) {

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if (restaurantOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        }

        Restaurant restaurant = restaurantOptional.get();

        restaurant.setPeanutScore(updatedRestaurant.getPeanutScore());
        restaurant.setDairyScore(updatedRestaurant.getDairyScore());
        restaurant.setEggScore(updatedRestaurant.getEggScore());

        restaurant.setName(updatedRestaurant.getName());
        restaurant.setWebsite(updatedRestaurant.getWebsite());
        restaurant.setCity(updatedRestaurant.getCity());
        restaurant.setOverallScore(updatedRestaurant.getOverallScore());
        restaurant.setZipCode(updatedRestaurant.getZipCode());
        restaurant.setState(updatedRestaurant.getState());

        restaurantRepository.save(restaurant);

    }


    @GetMapping
    public Iterable<Restaurant> getAllRestaurants() {

        return restaurantRepository.findAll();
    }


    @GetMapping("/search")
    public Iterable<Restaurant> searchRestaurants(@RequestParam(required = false) String zipcode, @RequestParam(required = false) String allergy) {
        if (!ObjectUtils.isEmpty(zipcode)) {
            validateZipCode(zipcode);
        }

        Iterable<Restaurant> restaurants;
        if ("peanut".equalsIgnoreCase(allergy)) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(zipcode);
        } else if ("egg".equalsIgnoreCase(allergy)) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(zipcode);
        } else if ("dairy".equalsIgnoreCase(allergy)) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(zipcode);
        } else {
            restaurants = restaurantRepository.findAll();
        }

        return restaurants;
    }


}


