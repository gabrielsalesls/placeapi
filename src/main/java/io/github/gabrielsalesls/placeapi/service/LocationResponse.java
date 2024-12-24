package io.github.gabrielsalesls.placeapi.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationResponse {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("estado")
    private String state;

    public LocationResponse() {
    }

    public LocationResponse(String name, int id, String state) {
        this.name = name;
        this.id = id;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }
}
