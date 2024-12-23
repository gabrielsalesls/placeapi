package io.github.gabrielsalesls.placeapi.dto;

import io.github.gabrielsalesls.placeapi.entity.Place;

public record PlaceResponse(Long id, String name, String slug, String city, String state) {

    public PlaceResponse(Place place) {
        this(place.getId(), place.getName(), place.getSlug(), place.getCity(), place.getState());
    }
}
