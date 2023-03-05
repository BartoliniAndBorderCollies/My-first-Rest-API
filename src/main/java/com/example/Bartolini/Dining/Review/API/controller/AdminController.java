package com.example.Bartolini.Dining.Review.API.controller;

import com.example.Bartolini.Dining.Review.API.model.AdminDecision;
import com.example.Bartolini.Dining.Review.API.model.Restaurant;
import com.example.Bartolini.Dining.Review.API.model.Review;
import com.example.Bartolini.Dining.Review.API.model.ReviewStatus;
import com.example.Bartolini.Dining.Review.API.repository.RestaurantRepository;
import com.example.Bartolini.Dining.Review.API.repository.ReviewRepository;
import com.example.Bartolini.Dining.Review.API.repository.UserRepository;

import com.example.Bartolini.Dining.Review.API.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final AdminService adminService;



    public AdminController(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository, UserRepository userRepository, AdminService adminService) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.adminService = adminService;
    }

    @GetMapping("/reviews/{reviewStatusParam}")
    public List<Review> getReviewsByStatus(@PathVariable String reviewStatusParam) {
        ReviewStatus reviewStatus = ReviewStatus.PENDING;
        try {
            reviewStatus = ReviewStatus.valueOf(reviewStatusParam.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return reviewRepository.findReviewsByStatus(reviewStatus);
    }


    @PutMapping("/reviews/{reviewId}")
    public void reviewAction(@PathVariable Long reviewId, @RequestBody AdminDecision adminDecision) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Review review = optionalReview.get();

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if (optionalRestaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (adminDecision.getAccept()) {
            review.setStatus(ReviewStatus.ACCEPTED);
        } else {
            review.setStatus(ReviewStatus.REJECTED);
        }
        reviewRepository.save(review);
        adminService.updateRestaurantReviewScores(optionalRestaurant.get());

    }



}
