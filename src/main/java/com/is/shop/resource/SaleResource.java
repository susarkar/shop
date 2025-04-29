package com.is.shop.resource;

import com.is.shop.entity.*; // Import necessary entities
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/api/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SaleResource {

    @GET
    public List<Sale> listAll() {
        // Eagerly fetch items or customer if needed frequently in the list view
        // Example: return Sale.list("SELECT DISTINCT s FROM Sale s LEFT JOIN FETCH s.items LEFT JOIN FETCH s.customer");
        return Sale.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        // Fetch related data eagerly for the detail view
        Optional<Sale> saleOpt = Sale.find("id = ?1 LEFT JOIN FETCH s.items LEFT JOIN FETCH s.customer", id).firstResultOptional();
        return saleOpt
                .map(sale -> Response.ok(sale).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response create(Sale saleInput) {
        // --- Complex Logic Warning ---
        // This requires careful handling:
        // 1. Validate input (customer exists, products exist, quantities valid).
        // 2. Calculate totalAmount, taxAmount based on items.
        // 3. Link Customer entity.
        // 4. Create Sale entity.
        // 5. Create SaleItem entities, linking them to the Sale and Product.
        // 6. Update Product stockQuantity for each item sold.
        // 7. Ensure atomicity (all steps succeed or fail together).

        // --- Simplified Example (Assumes pre-calculated totals & valid IDs in input) ---
        if (saleInput == null || saleInput.id != null || saleInput.items == null || saleInput.items.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sale ID must be null, items required.")
                    .build();
        }

        // 1. Link Customer
        if (saleInput.customer != null && saleInput.customer.id != null) {
            Customer customer = Customer.findById(saleInput.customer.id);
            if (customer == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Customer ID").build();
            }
            saleInput.customer = customer;
        } else {
            saleInput.customer = null; // Allow sales without customer
        }

        // Persist Sale first to get an ID
        saleInput.persist();
        if (!saleInput.isPersistent()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to save sale header.").build();
        }

        // 2. Process Items & Update Stock (Simplified - lacks proper validation/calculation)
        for (SaleItem itemInput : saleInput.items) {
            if (itemInput.product == null || itemInput.product.id == null || itemInput.quantity == null || itemInput.quantity <= 0) {
                // Rollback needed - Transactional helps, but manual cleanup might be needed if not using exceptions
                throw new WebApplicationException("Invalid product data in sale item.", Response.Status.BAD_REQUEST);
            }

            Product product = Product.findById(itemInput.product.id);
            if (product == null) {
                throw new WebApplicationException("Product not found: ID " + itemInput.product.id, Response.Status.BAD_REQUEST);
            }

            // Basic stock check (real world needs locking/better concurrency)
            if (product.stockQuantity < itemInput.quantity) {
                throw new WebApplicationException("Insufficient stock for product: " + product.name, Response.Status.BAD_REQUEST);
            }

            // Update stock
            product.stockQuantity -= itemInput.quantity;

            // Create and link SaleItem
            itemInput.sale = saleInput; // Link back to the persisted Sale
            itemInput.product = product; // Link the managed Product entity
            itemInput.id = null; // Ensure ID is null for creation
            itemInput.persist();
        }

        // Recalculate totals on the server side for accuracy (example)
        // saleInput.totalAmount = calculateTotal(saleInput.items);
        // saleInput.taxAmount = calculateTax(saleInput.items);

        URI createdUri = UriBuilder.fromResource(SaleResource.class)
                .path(String.valueOf(saleInput.id)).build();
        return Response.created(createdUri).entity(saleInput).build(); // Return the potentially updated sale object
    }

    // PUT (Update) and DELETE for Sales are often complex or disallowed.
    // Updates might involve changing status, adding notes, etc.
    // Deleting might require reversing stock changes, which can be problematic.
    // Consider specific endpoints like /api/sales/{id}/cancel or /api/sales/{id}/status

}