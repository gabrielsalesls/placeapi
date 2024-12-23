package io.github.gabrielsalesls.placeapi.dto;

import io.github.gabrielsalesls.placeapi.entity.Place;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlaceRequest {

    @NotBlank(message = "Name cannot be null")
    private String name;

    @NotBlank(message = "State cannot be null")
    private String slug;

    @NotBlank(message = "City cannot be null")
    private String city;

    @NotBlank(message = "State cannot be null")
    @Size(min = 2, max = 2, message = "State must have exactly 2 characters")
    private String state;

    public PlaceRequest(String name, String slug, String city, String state) {
        this.name = name;
        this.slug = slug;
        this.city = city;
        this.state = state;
    }

    public Place toModel() {
        return new Place(this.name, this.state, this.city, this.slug);
    }
}
