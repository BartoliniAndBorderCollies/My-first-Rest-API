package com.example.Bartolini.Dining.Review.API.controller;

import com.example.Bartolini.Dining.Review.API.model.Restaurant;
import com.example.Bartolini.Dining.Review.API.model.Review;
import com.example.Bartolini.Dining.Review.API.model.ReviewStatus;
import com.example.Bartolini.Dining.Review.API.model.User;
import com.example.Bartolini.Dining.Review.API.repository.RestaurantRepository;
import com.example.Bartolini.Dining.Review.API.repository.ReviewRepository;
import com.example.Bartolini.Dining.Review.API.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
@RequestMapping("/review")
public class ReviewController {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;


    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addUserReview(@RequestBody Review review) {
        validateUserReview(review);
    Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if (optionalRestaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
            }
        review.setStatus(ReviewStatus.PENDING);
        reviewRepository.save(review);
    }


    public void validateUserReview(Review review) {
        if(ObjectUtils.isEmpty(review.getSubmittedBy())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if(ObjectUtils.isEmpty(review.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (ObjectUtils.isEmpty(review.getPeanutScore()) &&
                ObjectUtils.isEmpty(review.getDairyScore()) &&
                ObjectUtils.isEmpty(review.getEggScore())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Optional<User> optionalUser = userRepository.findUserByDisplayName(review.getSubmittedBy());
        if(optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    public Iterable <Review> getAllReviews() {

        return reviewRepository.findAll();
    }


}
