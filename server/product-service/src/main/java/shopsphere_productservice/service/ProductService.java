package shopsphere_productservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shopsphere.shared.exceptions.NotFoundException;
import shopsphere_productservice.dto.response.PaginatedResponse;
import shopsphere_productservice.dto.response.ProductDto;
import shopsphere_productservice.model.Product;
import shopsphere_productservice.repository.ProductRepository;
import shopsphere_productservice.utils.ProductUtil;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDto getProductById(String productID) {
        Product product = findByID(productID);

        return ProductUtil.mapToDto(product);
    }

    public PaginatedResponse<ProductDto> getProducts(String category, Pageable pageable) {
        if (category == null || category.isBlank()) {
            Page<Product> products = productRepository.findAll(pageable);

            return new PaginatedResponse<>(products.map(ProductUtil::mapToDto));
        }

        Page<Product> products = productRepository.findByCategory(category, pageable);
        return new PaginatedResponse<>(products.map(ProductUtil::mapToDto));
    }

    private Product findByID(String productID) {
        return productRepository.findById(productID)
                .orElseThrow(() -> new NotFoundException("product not found"));
    }
}
