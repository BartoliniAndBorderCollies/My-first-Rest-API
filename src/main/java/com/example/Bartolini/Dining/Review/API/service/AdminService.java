package com.example.Bartolini.Dining.Review.API.service;

import com.example.Bartolini.Dining.Review.API.model.Restaurant;
import com.example.Bartolini.Dining.Review.API.model.Review;
import com.example.Bartolini.Dining.Review.API.model.ReviewStatus;
import com.example.Bartolini.Dining.Review.API.repository.RestaurantRepository;
import com.example.Bartolini.Dining.Review.API.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.util.List;

@Service
public class AdminService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public AdminService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
    }


    public void updateRestaurantReviewScores(Restaurant restaurant) {
        List<Review> reviews = reviewRepository.findReviewsByRestaurantIdAndStatus(restaurant.getId(), ReviewStatus.ACCEPTED);
        if (reviews.size() == 0) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
        }

        int peanutSum = 0;
        int peanutCount = 0;
        int dairySum = 0;
        int dairyCount = 0;
        int eggSum = 0;
        int eggCount = 0;
        for (Review r : reviews) {
            if (!ObjectUtils.isEmpty(r.getPeanutScore())) {
                peanutSum += r.getPeanutScore();
                peanutCount++;
            }
            if (!ObjectUtils.isEmpty(r.getDairyScore())) {
                dairySum += r.getDairyScore();
                dairyCount++;
            }
            if ((!ObjectUtils.isEmpty(r.getEggScore()))) {
                eggSum += r.getEggScore();
                eggCount++;
            }
        }

        int totalCount = peanutCount + eggCount + dairyCount;
        int totalSum = peanutSum + eggSum + dairySum;

        float overallScore = (float) totalSum / totalCount;
        restaurant.setOverallScore(decimalFormat.format(overallScore));

        if (peanutCount > 0) {
            float peanutScore = (float) peanutSum / peanutCount;
            restaurant.setPeanutScore(decimalFormat.format(peanutScore));
        }
        if (eggCount > 0) {
            float eggScore = (float) eggSum / eggCount;
            restaurant.setEggScore(decimalFormat.format(eggScore));
        }
        if (dairyCount > 0) {
            float dairyScore = (float) dairySum / dairyCount;
            restaurant.setDairyScore(decimalFormat.format(dairyScore));
        }
        restaurantRepository.save(restaurant);


    }


}
