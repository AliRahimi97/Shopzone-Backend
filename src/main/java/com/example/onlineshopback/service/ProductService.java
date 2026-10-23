package com.example.onlineshopback.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.onlineshopback.dto.ProductDTO;
import com.example.onlineshopback.mapper.ProductMapper;
import com.example.onlineshopback.model.CategoryEnum;
import com.example.onlineshopback.model.Product;
import com.example.onlineshopback.model.ProductImage;
import com.example.onlineshopback.model.Users;
import com.example.onlineshopback.repository.ProductImageRepository;
import com.example.onlineshopback.repository.ProductRepository;
import com.example.onlineshopback.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // ==================== اضافه کردن محصول ====================
    @Transactional
    public ProductDTO addProduct(Product product, Integer adminId, String imageUrl) {

        Users admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id : " + adminId));

        product.setCreatedBy(admin);

        Product savedProduct = productRepository.save(product);

        // ساخت اولین عکس (primary)
        ProductImage productImage = new ProductImage();
        productImage.setImageUrl("/uploads/products/" + imageUrl);
        productImage.setPrimaryImage(true);
        productImage.setDisplayOrder(0);
        productImage.setProduct(savedProduct);

        productImageRepository.save(productImage);

        // برای اینکه لیست images پر بشه، دوباره لود می‌کنیم
        savedProduct = productRepository.findById(savedProduct.getProductId())
                .orElseThrow();

        return ProductMapper.toProductDTO(savedProduct);
    }

    // ==================== گرفتن همه محصولات ====================
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProduct() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        return ProductMapper.toProductDTO(product);
    }


    @Transactional
    public ProductDTO updateProduct(
            Integer id,
            String name,
            String description,
            Double price,
            Integer stock,
            CategoryEnum category,
            String brand,
            Integer discountPercent,
            Boolean active,
            MultipartFile image
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        product.setBrand(brand);
        product.setDiscountPercent(discountPercent);
        product.setActive(active);

        if (image != null && !image.isEmpty()) {
            try {
                String filename = fileStorageService.saveFile(image);

                ProductImage newImage = new ProductImage();
                newImage.setImageUrl("/uploads/products/" + filename);
                newImage.setPrimaryImage(false); 
                newImage.setDisplayOrder(product.getImages().size()); 
                newImage.setProduct(product);

                productImageRepository.save(newImage);

            } catch (Exception e) {
                throw new RuntimeException("Image upload failed", e);
            }
        }

        Product saved = productRepository.save(product);
        return ProductMapper.toProductDTO(saved);
    }

    @Transactional
    public ProductDTO addImageToProduct(Integer productId, MultipartFile image, Boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        try {
            String filename = fileStorageService.saveFile(image);

            if (Boolean.TRUE.equals(isPrimary)) {
                product.getImages().forEach(img -> img.setPrimaryImage(false));
            }

            ProductImage productImage = new ProductImage();
            productImage.setImageUrl("/uploads/products/" + filename);
            productImage.setPrimaryImage(Boolean.TRUE.equals(isPrimary));
            productImage.setDisplayOrder(product.getImages() != null ? product.getImages().size() : 0);
            productImage.setProduct(product);

            productImageRepository.save(productImage);

            Product updated = productRepository.findByIdWithImages(productId)
                    .orElseThrow();
            return ProductMapper.toProductDTO(updated);

        } catch (Exception e) {
            throw new RuntimeException("Failed to add image", e);
        }
    }

    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (Boolean.TRUE.equals(image.getPrimaryImage())) {
            List<ProductImage> otherImages = productImageRepository
                    .findByProductProductId(image.getProduct().getProductId());

            otherImages.stream()
                    .filter(img -> !img.getId().equals(imageId))
                    .findFirst()
                    .ifPresent(img -> {
                        img.setPrimaryImage(true);
                        productImageRepository.save(img);
                    });
        }

        productImageRepository.delete(image);
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public List<ProductDTO> getDicountProducts(Double minPrice) {
        return productRepository.getDiscountProducts(minPrice)
                .stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> GetHotTopics(Integer stock) {
        return productRepository.findByStockLessThan(stock)
                .stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductByBrand(String brand) {
        return productRepository.findByBrandIgnoreCase(brand)
                .stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductByCategory(CategoryEnum category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    @Transactional (readOnly = true)
    public List<ProductDTO> searchProducts(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        String term = q.trim();
        return productRepository.searchActive(term).stream()
                .map(ProductMapper::toProductDTO)
                .toList();
    }
}