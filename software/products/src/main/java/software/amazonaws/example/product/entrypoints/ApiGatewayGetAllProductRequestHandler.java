// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.entrypoints;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazonaws.example.product.model.Products;
import software.amazonaws.example.product.store.ProductStore;
import software.amazonaws.example.product.store.dynamodb.DynamoDbProductStore;

import java.util.Map;
import java.util.Optional;

import static software.amazon.awssdk.http.Header.CONTENT_TYPE;

public class ApiGatewayGetAllProductRequestHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayGetAllProductRequestHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductStore productStore;

    public ApiGatewayGetAllProductRequestHandler() {
        this(new DynamoDbProductStore());
    }

    public ApiGatewayGetAllProductRequestHandler(ProductStore productStore) {
        this.productStore = productStore;
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        Optional<Products> products;
        try {
            products = productStore.getAllProduct();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withHeaders(Map.of(CONTENT_TYPE, "application/json"))
                    .withBody("{\"message\": \"Failed to get products\"}")
                    .build();
        }

        if (products.isEmpty()) {
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withHeaders(Map.of(CONTENT_TYPE, "application/json"))
                    .withBody("{\"message\": \"Failed to get products\"}")
                    .build();
        }

        try {
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withHeaders(Map.of(CONTENT_TYPE, "application/json"))
                    .withBody(objectMapper.writeValueAsString(products.get()))
                    .build();
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .build();
        }
    }
}
