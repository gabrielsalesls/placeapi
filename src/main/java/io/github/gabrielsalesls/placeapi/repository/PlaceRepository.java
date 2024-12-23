package io.github.gabrielsalesls.placeapi.repository;

import io.github.gabrielsalesls.placeapi.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

}
