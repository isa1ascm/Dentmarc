package com.example.Dentmarc.api;

import com.example.Dentmarc.api.ApiDtos.CartItemRequest;
import com.example.Dentmarc.api.ApiDtos.CartQuantityRequest;
import com.example.Dentmarc.api.ApiDtos.CartResponse;
import com.example.Dentmarc.service.ApiCartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@Validated
public class CartApiController {
    private final ApiCartService apiCartService;

    public CartApiController(ApiCartService apiCartService) {
        this.apiCartService = apiCartService;
    }

    @GetMapping
    public CartResponse get(Authentication authentication) {
        return apiCartService.getCart(authentication.getName());
    }

    @PostMapping("/items")
    public CartResponse add(Authentication authentication, @Valid @RequestBody CartItemRequest request) {
        return apiCartService.addItem(authentication.getName(), request.productId(), request.cantidad());
    }

    @PutMapping("/items/{productId}")
    public CartResponse update(Authentication authentication, @PathVariable @Positive Long productId,
                               @Valid @RequestBody CartQuantityRequest request) {
        return apiCartService.updateItem(authentication.getName(), productId, request.cantidad());
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse remove(Authentication authentication, @PathVariable @Positive Long productId) {
        return apiCartService.removeItem(authentication.getName(), productId);
    }
}
