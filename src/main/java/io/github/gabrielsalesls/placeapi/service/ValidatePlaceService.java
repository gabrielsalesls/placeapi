package io.github.gabrielsalesls.placeapi.service;

import io.github.gabrielsalesls.placeapi.dto.LocationResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class ValidatePlaceService {

    @Value("${app.places.domain.cptecapi}")
    String host;

    private final RestClient restClient;

    @Autowired
    public ValidatePlaceService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void validateCityAndState(@NotNull String city, @NotNull String state) {

        String uri = String.format(host + "/api/cptec/v1/cidade/%s", city);

        List<LocationResponse> localidadesResponse = restClient.get()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new IllegalArgumentException(response.getStatusText());
                }))
                .body(new ParameterizedTypeReference<List<LocationResponse>>() {
                });

        localidadesResponse.stream().filter(
                        location -> location.getState().equalsIgnoreCase(state))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cidade não existe no Estado enviado"));
    }
}
