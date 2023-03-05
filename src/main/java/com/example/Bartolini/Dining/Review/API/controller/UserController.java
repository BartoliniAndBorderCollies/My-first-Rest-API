package com.example.Bartolini.Dining.Review.API.controller;

import com.example.Bartolini.Dining.Review.API.model.Restaurant;
import com.example.Bartolini.Dining.Review.API.model.User;
import com.example.Bartolini.Dining.Review.API.repository.UserRepository;

import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RequestMapping("/users")
@RestController

public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{displayName}")
    public User getUserByDisplayName(@PathVariable String displayName) {
        validateDisplayName(displayName);

        Optional<User> optionalExistingUser = userRepository.findUserByDisplayName(displayName);
        if (optionalExistingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User existingUser = optionalExistingUser.get();
        existingUser.setId(null);

        return existingUser;


    }

    private void validateDisplayName(String displayName) {
        if (ObjectUtils.isEmpty(displayName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@RequestBody User user) {
        validateUser(user);
        userRepository.save(user);
    }

    private void validateUser(User user) {
        validateDisplayName(user.getDisplayName());

        Optional<User> existingUser = userRepository.findUserByDisplayName(user.getDisplayName());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }


    @DeleteMapping("/{displayName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User deleteUser(@PathVariable("displayName") String displayName) {
        validateDisplayName(displayName);


        Optional<User> userToDeleteOptional = this.userRepository.findUserByDisplayName(displayName);
        if (userToDeleteOptional.isEmpty()) {
            return null;
        }
        User userToDelete = userToDeleteOptional.get();

        this.userRepository.delete(userToDelete);
        return userToDelete;
    }


    public void updateUserInfo(User updatedUser, User existingUser) {
        if (ObjectUtils.isEmpty(updatedUser.getDisplayName())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!ObjectUtils.isEmpty(updatedUser.getDisplayName())) {
            existingUser.setDisplayName(updatedUser.getDisplayName());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getCity())) {
            existingUser.setCity(updatedUser.getCity());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getState())) {
            existingUser.setState(updatedUser.getState());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getZipCode())) {
            existingUser.setZipCode((updatedUser.getZipCode()));
        }
        if (!ObjectUtils.isEmpty(updatedUser.getPeanutInterested())) {
            existingUser.setPeanutInterested(updatedUser.getPeanutInterested());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getEggInterested())) {
            existingUser.setEggInterested(updatedUser.getEggInterested());
        }
        if (!ObjectUtils.isEmpty(updatedUser.getDairyInterested())) {
            existingUser.setDairyInterested(updatedUser.getDairyInterested());
        }
    }

    @PutMapping("/{displayName}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable String displayName, @RequestBody User updatedUser) {
        validateDisplayName(displayName);

        Optional<User> optionalExistingUser = userRepository.findUserByDisplayName(displayName);
        if (optionalExistingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        }

        User existingUser = optionalExistingUser.get();
        updateUserInfo(updatedUser, existingUser);
        userRepository.save(existingUser);
    }

    @GetMapping
    public Iterable<User> getAllUsers() {

        return userRepository.findAll();
    }


}

