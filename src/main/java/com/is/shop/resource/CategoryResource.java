package com.is.shop.resource;

import com.is.shop.entity.Category;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped // Make it a CDI bean
public class CategoryResource {

    @GET
    public List<Category> listAll() {
        System.out.println("Calling list of all ");

        return Category.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return Category.<Category>findByIdOptional(id)
                .map(category -> Response.ok(category).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional // Needed for database modifications
    public Response create(Category category) {
        if (category == null || category.id != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Category ID must be null for creation.")
                    .build();
        }
        category.persist();
        if (category.isPersistent()) {
            URI createdUri = UriBuilder.fromResource(CategoryResource.class)
                    .path(String.valueOf(category.id)).build();
            return Response.created(createdUri).entity(category).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to persist category.")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional // Needed for database modifications
    public Response update(@PathParam("id") Integer id, Category categoryUpdate) {
        if (categoryUpdate == null || categoryUpdate.name == null || categoryUpdate.name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Category name cannot be blank for update.")
                    .build();
        }

        Category category = Category.findById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Update fields
        category.name = categoryUpdate.name;
      //  category.description = categoryUpdate.description;
        // Panache automatically persists changes within a transaction

        return Response.ok(category).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional // Needed for database modifications
    public Response delete(@PathParam("id") Integer id) {
        boolean deleted = Category.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            // Could be because it didn't exist or due to constraints
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}