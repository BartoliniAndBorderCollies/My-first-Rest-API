package com.example.Bartolini.Dining.Review.API.repository;

import com.example.Bartolini.Dining.Review.API.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByDisplayName(String displayName);

}
