package com.example.Bartolini.Dining.Review.API.repository;

import com.example.Bartolini.Dining.Review.API.model.Review;
import com.example.Bartolini.Dining.Review.API.model.ReviewStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {

    List<Review> findReviewsByRestaurantIdAndStatus(Long restaurantId, ReviewStatus reviewStatus);
    List<Review> findReviewsByStatus(ReviewStatus reviewStatus);

}
