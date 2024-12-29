package io.github.gabrielsalesls.placeapi.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.github.gabrielsalesls.placeapi.dto.PlaceRequest;
import io.github.gabrielsalesls.placeapi.entity.Place;
import io.github.gabrielsalesls.placeapi.infrastructure.FileUtils;
import io.github.gabrielsalesls.placeapi.repository.PlaceRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
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
import static org.hamcrest.Matchers.containsString;
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
    void shouldSaveAValidPlaceAndReturnStatusCreated() {

        String response = fileUtils.loadFileContents("wiremock/responses/cptec_place_barueri_response.json");

        wireMockServer.stubFor(post(urlEqualTo("/api/cptec/v1/cidade/Barueri"))
                .willReturn(aResponse()
                        .withBody(response)
                        .withStatus(200)));

        var placeRequest = new PlaceRequest("Parque", "parque-municipal", "Barueri", "SP");

        given()
                .contentType("application/json")
                .body(placeRequest)
                .when()
                .post("/api/v1/places")
                .then()
                .statusCode(HttpStatus.CREATED.value());

    }

    @Test
    void shoudReturnOkWhenPlaceIsFoundById() {

        Place place = new Place("Parquinho", "SP", "Osasco", "parquinho");

        Long placeIdSaved = placeRepository.save(place).getId();

        given()
                .contentType(ContentType.JSON)
                .pathParams("id", placeIdSaved)
                .when()
                .get("/api/v1/places/{id}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("name", Matchers.equalTo("Parquinho"));
    }

    @Test
    void shouldReturnBadRequestWhenFindByIdIsNotFound() {

        given()
                .contentType(ContentType.JSON)
                .pathParams("id", "1")
                .when()
                .get("/api/v1/places/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString("Id não encontrado"));
    }

    @Test
    void shouldReturnOkWhenSearchByNameIsFound() {
        Place place = new Place("Parquinho", "SP", "Osasco", "parquinho");

        String placeSavedName = placeRepository.save(place).getName();

        given()
                .contentType(ContentType.JSON)
                .queryParam("name", placeSavedName)
                .when()
                .get("/api/v1/places/")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(1));
    }

    @Test
    void shouldReturnEmptyListWhenSearchByNameIsNotFound() {

        given()
                .contentType(ContentType.JSON)
                .queryParam("name", "randomname")
                .when()
                .get("/api/v1/places/")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(".", hasSize(0));
    }

    @Test
    void shoudReturnValidPlaceWhenEditDataIsValid() {

        Place placeToEdit = new Place("Parquinho", "SP", "Osasco", "parquinho");
        Long placeToEditId = placeRepository.save(placeToEdit).getId();

        String response = fileUtils.loadFileContents("wiremock/responses/cptec_place_barueri_response.json");

        wireMockServer.stubFor(post(urlEqualTo("/api/cptec/v1/cidade/Barueri"))
                .willReturn(aResponse()
                        .withBody(response)
                        .withStatus(200)));

        PlaceRequest placeRequest = new PlaceRequest("Parque", "Parque", "Barueri", "SP");

        given()
                .contentType(ContentType.JSON)
                .pathParams("id", placeToEditId)
                .body(placeRequest)
                .when()
                .put("/api/v1/places/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("city", Matchers.equalTo("Barueri"));

    }

    @Test
    void shouldReturnBadRequestWhenIdToEditIsNotFound() {

        PlaceRequest placeRequest = new PlaceRequest("Parque", "Parque", "Barueri", "SP");
        String randomId = "123";

        given()
                .contentType(ContentType.JSON)
                .pathParams("id", randomId)
                .body(placeRequest)
                .when()
                .put("/api/v1/places/{id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString("Id Invalido"));
    }
}