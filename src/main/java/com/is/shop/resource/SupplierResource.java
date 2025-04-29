package com.is.shop.resource;

import com.is.shop.entity.Supplier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;

@Path("/api/suppliers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SupplierResource {

    @GET
    public List<Supplier> listAll() {
        return Supplier.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return Supplier.<Supplier>findByIdOptional(id)
                .map(supplier -> Response.ok(supplier).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response create(Supplier supplier) {
        if (supplier == null || supplier.id != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Supplier ID must be null for creation.")
                    .build();
        }
        supplier.persist();
        if (supplier.isPersistent()) {
            URI createdUri = UriBuilder.fromResource(SupplierResource.class)
                    .path(String.valueOf(supplier.id)).build();
            return Response.created(createdUri).entity(supplier).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to persist supplier.")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, Supplier supplierUpdate) {
        if (supplierUpdate == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Supplier supplier = Supplier.findById(id);
        if (supplier == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Update fields
        supplier.name = supplierUpdate.name;
        supplier.phone = supplierUpdate.phone;
        supplier.email = supplierUpdate.email;
        supplier.address = supplierUpdate.address;
        supplier.gstin = supplierUpdate.gstin;
        // createdAt is usually not updated

        return Response.ok(supplier).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        // Consider if deleting a supplier should be allowed if they have purchase history
        boolean deleted = Supplier.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}