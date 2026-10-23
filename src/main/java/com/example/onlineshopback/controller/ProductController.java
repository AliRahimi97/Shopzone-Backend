package com.example.onlineshopback.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.onlineshopback.dto.ProductDTO;
import com.example.onlineshopback.dto.ProductImageDTO;
import com.example.onlineshopback.mapper.ProductMapper;
import com.example.onlineshopback.model.CategoryEnum;
import com.example.onlineshopback.model.ProductImage;
import com.example.onlineshopback.repository.ProductImageRepository;
import com.example.onlineshopback.service.FileStorageService;
import com.example.onlineshopback.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ProductImageRepository productImageRepository;

    // ==================== اضافه کردن محصول (با یک عکس اولیه) ====================
    @PostMapping("/add")
    public ResponseEntity<ProductDTO> addProduct(
            @RequestParam Integer adminId,
            @RequestParam("image") MultipartFile image,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam CategoryEnum category,
            @RequestParam String brand,
            @RequestParam(defaultValue = "0") Integer discountPercent
    ) {
        try {
            String fileName = fileStorageService.saveFile(image);

            com.example.onlineshopback.model.Product product = new com.example.onlineshopback.model.Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStock(stock);
            product.setCategory(category);
            product.setBrand(brand);
            product.setDiscountPercent(discountPercent);

            ProductDTO saved = productService.addProduct(product, adminId, fileName);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            throw new RuntimeException("Error uploading product", e);
        }
    }

    // ==================== اضافه کردن عکس به محصول موجود ====================
    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductDTO> addImageToProduct(
            @PathVariable Integer productId,
            @RequestParam("image") MultipartFile image,
            @RequestParam(defaultValue = "false") Boolean isPrimary
    ) {
        ProductDTO updated = productService.addImageToProduct(productId, image, isPrimary);
        return ResponseEntity.ok(updated);
    }

    // ==================== حذف یک عکس از گالری ====================
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("{productId}/images")
    public ResponseEntity<List<ProductImageDTO>> getProductImages(@PathVariable Integer productId) {
        List<ProductImage> images = productImageRepository.findByProductProductId(productId);

        List<ProductImageDTO> dtos = images.stream()
            .map(ProductMapper::toImageDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/all")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProduct();
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam CategoryEnum category,
            @RequestParam String brand,
            @RequestParam(defaultValue = "0") Integer discountPercent,
            @RequestParam(defaultValue = "true") Boolean active,
            @RequestParam(required = false) MultipartFile image
    ) {
        ProductDTO dto = productService.updateProduct(
                id, name, description, price, stock,
                category, brand, discountPercent, active, image
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/discount")
    public ResponseEntity<List<ProductDTO>> getDiscountproducts(@RequestParam Double minPrice) {
        return ResponseEntity.ok(productService.getDicountProducts(minPrice));
    }

    @GetMapping("/hot")
    public ResponseEntity<List<ProductDTO>> getHotProducts(@RequestParam Integer stock) {
        return ResponseEntity.ok(productService.GetHotTopics(stock));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductDTO>> getProductByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(productService.getProductByBrand(brand));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductByCategory(@PathVariable CategoryEnum category) {
        return ResponseEntity.ok(productService.getProductByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> search(@RequestParam String q) {
        return ResponseEntity.ok(productService.searchProducts(q));
    }
}