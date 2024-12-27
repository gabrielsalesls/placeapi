package io.github.gabrielsalesls.placeapi.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.github.gabrielsalesls.placeapi.entity.Place;
import io.github.gabrielsalesls.placeapi.infrastructure.FileUtils;
import io.github.gabrielsalesls.placeapi.repository.PlaceRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaceControllerIntegrationTest {

    @Autowired
    FileUtils fileUtils;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        int wireMockServerPort = 8081;
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(wireMockServerPort));
        wireMockServer.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        wireMockServer.stop();
    }

    @Autowired
    PlaceRepository placeRepository;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        placeRepository.deleteAll();
    }

    @Test
    void shouldGetAllPlaces() {
        List<Place> places = List.of(
                new Place("Shopping", "SP", "Barueri", "shopping"),
                new Place("Parquinho", "SP", "Osasco", "parquinho")
        );

        placeRepository.saveAll(places);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/places")
                .then()
                .statusCode(200)
                .body(".", hasSize(2));
    }

    @Test
    void shouldSaveAValidPlace() {

        String placeRequest = fileUtils.loadFileContents("wiremock/requests/valid_place_request.json");
        String response = fileUtils.loadFileContents("wiremock/responses/cptec_place_barueri_response.json");

        wireMockServer.stubFor(post(urlEqualTo("/api/cptec/v1/cidade/Barueri"))
                .willReturn(aResponse()
                        .withBody(response)
                        .withStatus(200)));

        given()
                .contentType(ContentType.JSON)
                .body(placeRequest)
                .when()
                .post("/api/v1/places")
                .then()
                .statusCode(HttpStatus.CREATED.value());

    }
}