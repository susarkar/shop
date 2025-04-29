package com.is.shop.resource;

import com.is.shop.entity.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;

@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CustomerResource {

    @GET
    public List<Customer> listAll() {
        return Customer.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return Customer.<Customer>findByIdOptional(id)
                .map(customer -> Response.ok(customer).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response create(Customer customer) {
        if (customer == null || customer.id != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Customer ID must be null for creation.")
                    .build();
        }
        customer.persist();
        if (customer.isPersistent()) {
            URI createdUri = UriBuilder.fromResource(CustomerResource.class)
                    .path(String.valueOf(customer.id)).build();
            return Response.created(createdUri).entity(customer).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to persist customer.")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, Customer customerUpdate) {
        if (customerUpdate == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Customer customer = Customer.findById(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Update fields
        customer.name = customerUpdate.name;
        customer.phone = customerUpdate.phone;
        customer.email = customerUpdate.email;
        customer.gstin = customerUpdate.gstin;
        // createdAt is usually not updated

        return Response.ok(customer).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        // Consider if deleting a customer should be allowed if they have sales history
        boolean deleted = Customer.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}