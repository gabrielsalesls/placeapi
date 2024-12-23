package io.github.gabrielsalesls.placeapi.service;

import io.github.gabrielsalesls.placeapi.entity.Place;
import io.github.gabrielsalesls.placeapi.repository.PlaceRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Place> findAll() {
        return repository.findAll();
    }

    public Optional<Place> findById(@NotNull Long id) {
        return repository.findById(id);
    }

    public List<Place> findByName(@NotNull String name) {
        return repository.findByNameIgnoreCase(name);
    }
}
