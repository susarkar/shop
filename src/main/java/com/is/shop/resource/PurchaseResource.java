package com.is.shop.resource;

import com.is.shop.entity.*; // Import necessary entities
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/api/purchases")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PurchaseResource {

    @GET
    public List<Purchase> listAll() {
        // Eager fetch if needed:
        // return Purchase.list("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.items LEFT JOIN FETCH p.supplier");
        return Purchase.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        Optional<Purchase> purchaseOpt = Purchase.find("id = ?1 LEFT JOIN FETCH p.items LEFT JOIN FETCH p.supplier", id).firstResultOptional();
        return purchaseOpt
                .map(purchase -> Response.ok(purchase).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response create(Purchase purchaseInput) {
        // --- Complex Logic Warning ---
        // Similar to Sales: Validate, link supplier, create Purchase,
        // create PurchaseItems, update Product stock (increase this time).

        // --- Simplified Example ---
        if (purchaseInput == null || purchaseInput.id != null || purchaseInput.items == null || purchaseInput.items.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Purchase ID must be null, items required.")
                    .build();
        }

        // 1. Link Supplier
        if (purchaseInput.supplier != null && purchaseInput.supplier.id != null) {
            Supplier supplier = Supplier.findById(purchaseInput.supplier.id);
            if (supplier == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Supplier ID").build();
            }
            purchaseInput.supplier = supplier;
        } else {
            // Supplier might be mandatory for purchases
            return Response.status(Response.Status.BAD_REQUEST).entity("Supplier ID is required for purchase.").build();
        }

        // Persist Purchase first
        purchaseInput.persist();
        if (!purchaseInput.isPersistent()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to save purchase header.").build();
        }

        // 2. Process Items & Update Stock
        for (PurchaseItem itemInput : purchaseInput.items) {
            if (itemInput.product == null || itemInput.product.id == null || itemInput.quantity == null || itemInput.quantity <= 0) {
                throw new WebApplicationException("Invalid product data in purchase item.", Response.Status.BAD_REQUEST);
            }

            Product product = Product.findById(itemInput.product.id);
            if (product == null) {
                // Option: Create product if not found? Depends on requirements.
                throw new WebApplicationException("Product not found: ID " + itemInput.product.id, Response.Status.BAD_REQUEST);
            }

            // Update stock (Increase)
            product.stockQuantity += itemInput.quantity;
            // Update purchase price if needed?
            // product.purchasePrice = itemInput.price;

            // Create and link PurchaseItem
            itemInput.purchase = purchaseInput; // Link back to the persisted Purchase
            itemInput.product = product; // Link the managed Product entity
            itemInput.id = null; // Ensure ID is null for creation
            itemInput.persist();
        }

        // Recalculate totals on server side

        URI createdUri = UriBuilder.fromResource(PurchaseResource.class)
                .path(String.valueOf(purchaseInput.id)).build();
        return Response.created(createdUri).entity(purchaseInput).build();
    }

    // PUT/DELETE for Purchases often involves status changes rather than full modification/deletion.
}