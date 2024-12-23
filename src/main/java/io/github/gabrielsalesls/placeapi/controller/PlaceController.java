package io.github.gabrielsalesls.placeapi.controller;

import io.github.gabrielsalesls.placeapi.dto.PlaceRequest;
import io.github.gabrielsalesls.placeapi.dto.PlaceResponse;
import io.github.gabrielsalesls.placeapi.service.PlaceService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "api/v1/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PlaceResponse> create(@RequestBody @Valid PlaceRequest request) {
        return new ResponseEntity<>(new PlaceResponse(placeService.save(request.toModel())), HttpStatus.CREATED);
    }
}
