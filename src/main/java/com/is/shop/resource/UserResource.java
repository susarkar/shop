package com.is.shop.resource;

import com.is.shop.entity.User;
// Import a DTO (Data Transfer Object) if you want to avoid exposing password hash
// import com.is.shop.resource.dto.UserDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserResource {

    // --- IMPORTANT: Avoid exposing password hash ---
    // Option 1: Use a DTO (Data Transfer Object)
    // Option 2: Select specific fields (less clean with Panache static methods)
    // Option 3: Set passwordHash to null before returning (shown below, DTO is better)

    @GET
    public List<User> listAll() {
        List<User> users = User.listAll();
        // Nullify password hash before sending response
        users.forEach(u -> u.passwordHash = null);
        return users;
        // With DTO: return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return User.<User>findByIdOptional(id)
                .map(user -> {
                    user.passwordHash = null; // Nullify hash
                    return Response.ok(user).build();
                    // With DTO: return Response.ok(UserDto.fromEntity(user)).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response create(User user) {
        // --- Password Hashing ---
        // You MUST hash the password before persisting.
        // This should happen in a service layer ideally.
        // Example: user.passwordHash = passwordHashingService.hash(user.passwordHash);
        if (user == null || user.id != null || user.username == null || user.passwordHash == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User ID must be null, username and password required.")
                    .build();
        }
        // --- Add password hashing logic here ---
        // Example Placeholder:
        if (!user.passwordHash.startsWith("hashed_")) { // Simple check, replace with real hashing
            return Response.status(Response.Status.BAD_REQUEST).entity("Password must be pre-hashed for storage.").build();
        }

        user.persist();
        if (user.isPersistent()) {
            user.passwordHash = null; // Don't return hash
            URI createdUri = UriBuilder.fromResource(UserResource.class)
                    .path(String.valueOf(user.id)).build();
            return Response.created(createdUri).entity(user).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to persist user.")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, User userUpdate) {
        if (userUpdate == null || userUpdate.username == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username is required for update.")
                    .build();
        }

        User user = User.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Update fields - DO NOT update password hash here unless intended
        // Create a separate endpoint like /api/users/{id}/password for password changes
        user.username = userUpdate.username;
        user.name = userUpdate.name;
        user.role = userUpdate.role;
        user.status = userUpdate.status;

        user.passwordHash = null; // Don't return hash
        return Response.ok(user).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        // Consider soft delete (setting status to 'inactive') instead of hard delete
        boolean deleted = User.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}