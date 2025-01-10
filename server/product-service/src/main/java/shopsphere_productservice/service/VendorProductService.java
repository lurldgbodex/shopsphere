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
import shopsphere_shared.utils.RoleUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VendorProductService {

    private final RoleUtils roleUtil;
    private final ProductUtil productUtil;
    private final ProductRepository productRepository;

    public ProductDto createProduct(CreateRequest request, HttpHeaders headers) {
        roleUtil.verifyRole(headers, Role.VENDOR);

        String vendorId = headers.getFirst("X-User-id");
        if (vendorId.isBlank()) {
            throw new BadRequestException("missing required data - userId");
        }

        Product newProduct = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .category(request.getCategory())
                .vendorId(vendorId)
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(newProduct);

        return productUtil.mapToDto(newProduct);
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
        roleUtil.verifyRole(headers, Role.VENDOR);

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
        return productUtil.mapToDto(product);
    }

    public void deleteProduct(String productId, HttpHeaders headers) {
        roleUtil.verifyRole(headers, Role.VENDOR);

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

        if (userId.isBlank()) {
            throw new MissingHeaderException("missing required data - userID");
        }

        return userId;
    }
}
