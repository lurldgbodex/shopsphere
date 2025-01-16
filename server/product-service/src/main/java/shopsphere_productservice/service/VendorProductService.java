package shopsphere_productservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import shopsphere_productservice.dto.request.CreateRequest;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.model.Product;
import shopsphere_productservice.repository.ProductRepository;
import shopsphere_productservice.utils.ProductUtil;
import shopsphere_shared.Role;
import shopsphere_shared.exceptions.*;
import shopsphere_shared.utils.RoleUtil;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorProductService {

    private final ProductRepository productRepository;

    public ProductDto createProduct(CreateRequest request, HttpHeaders headers) {
        RoleUtil.verifyRole(headers, List.of(Role.VENDOR));

        String vendorId = retrieveUserId(headers);

        Product newProduct = Product.builder()
                .vendorId(vendorId)
                .name(request.getName())
                .price(request.getPrice())
                .images(request.getImages())
                .quantity(request.getQuantity())
                .category(request.getCategory())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        newProduct = productRepository.save(newProduct);

        return ProductUtil.mapToDto(newProduct);
    }

    public void updateInventory(String productId, int quantityDelta) {
        Product product = findByID(productId);

        int updatedQuantity = product.getQuantity() + quantityDelta;
        if (updatedQuantity < 0) {
            throw new ConflictException("insufficient inventory");
        }

        product.setQuantity(updatedQuantity);
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
    }

    public ProductDto updateProduct(String productId, ProductDto request, HttpHeaders headers) {
        RoleUtil.verifyRole(headers, List.of(Role.VENDOR));

        String userId = retrieveUserId(headers);

        Product product = findByID(productId);
        validateAuthority(product.getVendorId(), userId);

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }

        product.setUpdatedAt(request.getUpdated_at());

        productRepository.save(product);
        return ProductUtil.mapToDto(product);
    }

    public void deleteProduct(String productId, HttpHeaders headers) {
        RoleUtil.verifyRole(headers, List.of(Role.VENDOR));

        Product product = findByID(productId);
        String userID = retrieveUserId(headers);

        validateAuthority(product.getVendorId(), userID);

        productRepository.delete(product);
    }

    private void validateAuthority(String vendorId, String userID) {
        if (!vendorId.equals(userID)) {
            throw new ForbiddenException("You do not have authorization to perform action");
        }
    }

    private Product findByID(String productID) {
        return productRepository.findById(productID)
                .orElseThrow(() -> new NotFoundException("product not found"));
    }

    private String retrieveUserId(HttpHeaders headers) {
        String userId = headers.getFirst("X-User-Id");

        try {
            if (userId.isBlank()) {
                throw new MissingHeaderException("missing required data - userID");
            }
        } catch (NullPointerException ex) {
            throw new MissingHeaderException("missing required data - userID");
        }

        return userId;
    }
}
