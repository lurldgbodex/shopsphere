package shopsphere_productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopsphere_productservice.dto.request.CreateRequest;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.service.VendorProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vendor/products")
public class ProductVendorController {

    private final VendorProductService vendorProductService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid CreateRequest request,
                                                    @RequestHeader HttpHeaders headers) {

        ProductDto product = vendorProductService.createProduct(request, headers);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String productId,
                                                    @RequestBody ProductDto request,
                                                    @RequestHeader HttpHeaders headers) {

        return ResponseEntity.ok(vendorProductService
                .updateProduct(productId, request, headers));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId,
                                              @RequestHeader HttpHeaders headers) {
        vendorProductService.deleteProduct(productId, headers);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/inventory")
    public ResponseEntity<Void> updateInventory(@PathVariable String productId,
                                                @RequestParam int quantityDelta) {

        vendorProductService.updateInventory(productId, quantityDelta);
        return ResponseEntity.noContent().build();
    }
}
