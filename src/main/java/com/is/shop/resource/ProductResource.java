package com.is.shop.resource;

import com.is.shop.dto.ProductDto;
import com.is.shop.entity.Category;
import com.is.shop.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ProductResource {

    @GET
    public List<ProductDto> listAll() {
        List<Product> products = Product.listAll();
        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        // Fetch category eagerly if needed often, otherwise lazy is fine
        Optional<Product> productOpt = Product.findByIdOptional(id);
        return productOpt
                .map(ProductDto::fromEntity)
                .map(productDto -> Response.ok(productDto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response create(ProductDto productDto) {
        if (productDto == null || productDto.id != null || productDto.sku == null || productDto.name == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Product ID must be null, SKU and Name required for creation.")
                    .build();
        }
Product product = ProductDto.toEntity(productDto);
        // Handle category linking - assumes category ID is provided in the request product object
        if ( productDto.categoryId != null) {
            Category category = Category.findById(productDto.categoryId);
            if (category == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Category ID provided: " + product.category.id)
                        .build();
            }
            product.category = category; // Link the managed entity
        } else {
            product.category = null; // Or handle as required if category is mandatory
        }
        product.persist();
        if (product.isPersistent()) {
            URI createdUri = UriBuilder.fromResource(ProductResource.class)
                    .path(String.valueOf(product.id)).build();
            return Response.created(createdUri).entity(product).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to persist product.")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, Product productUpdate) {
        if (productUpdate == null || productUpdate.sku == null || productUpdate.name == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Product SKU and Name required for update.")
                    .build();
        }

        Product product = Product.findById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Handle category linking
        if (productUpdate.category != null && productUpdate.category.id != null) {
            Category category = Category.findById(productUpdate.category.id);
            if (category == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid Category ID provided for update: " + productUpdate.category.id)
                        .build();
            }
            product.category = category;
        } else {
            product.category = null; // Allow removing category link
        }

        // Update fields
        product.name = productUpdate.name;
        product.sku = productUpdate.sku;
        product.purchasePrice = productUpdate.purchasePrice;
        product.salePrice = productUpdate.salePrice;
        product.stockQuantity = productUpdate.stockQuantity;
        product.taxRate = productUpdate.taxRate;
        product.barcode = productUpdate.barcode;
        // createdAt is usually not updated

        return Response.ok(product).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        // Consider implications: deleting a product might break sale/purchase history
        // Maybe implement soft delete (add a 'status' or 'is_deleted' field) instead
        boolean deleted = Product.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}