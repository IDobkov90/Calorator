package com.example.calorator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

public class RestResponsePage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePage(@JsonProperty("_embedded") JsonNode embedded,
                            @JsonProperty("page") JsonNode pageInfo) {
        super(
                embedded != null && embedded.has("foodItemDTOList")
                        ? new ObjectMapper().convertValue(embedded.get("foodItemDTOList"), new TypeReference<List<T>>() {
                })
                        : new ArrayList<>(),
                PageRequest.of(
                        pageInfo.get("number").asInt(),
                        pageInfo.get("size").asInt()
                ),
                pageInfo.get("totalElements").asLong()
        );
    }

    public RestResponsePage() {
        super(new ArrayList<>());
    }
}