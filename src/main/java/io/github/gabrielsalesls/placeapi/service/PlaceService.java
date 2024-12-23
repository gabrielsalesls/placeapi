package io.github.gabrielsalesls.placeapi.service;

import io.github.gabrielsalesls.placeapi.entity.Place;
import io.github.gabrielsalesls.placeapi.repository.PlaceRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {

    PlaceRepository repository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {
        this.repository = placeRepository;
    }

    public Place save(@NotNull Place place) {
        return repository.save(place);
    }

}
