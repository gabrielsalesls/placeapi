package io.github.gabrielsalesls.placeapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
public class Place {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @NotBlank(message = "Name cannot be null")
    private String name;

    @NotBlank(message = "State cannot be null")
    @Size(min = 2, max = 2, message = "State must have exactly 2 characters")
    private String state;

    @NotBlank(message = "City cannot be null")
    private String city;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
