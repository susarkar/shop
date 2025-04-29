package com.is.shop.resource;

import com.is.shop.entity.Expense;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;

@Path("/api/expenses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ExpenseResource {

    @GET
    public List<Expense> listAll() {
        return Expense.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return Expense.<Expense>findByIdOptional(id)
                .map(expense -> Response.ok(expense).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response create(Expense expense) {
        if (expense == null || expense.id != null || expense.amount == null || expense.date == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Expense ID must be null, amount and date required.")
                    .build();
        }
        expense.persist();
        if (expense.isPersistent()) {
            URI createdUri = UriBuilder.fromResource(ExpenseResource.class)
                    .path(String.valueOf(expense.id)).build();
            return Response.created(createdUri).entity(expense).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to persist expense.")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, Expense expenseUpdate) {
        if (expenseUpdate == null || expenseUpdate.amount == null || expenseUpdate.date == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Expense amount and date required for update.")
                    .build();
        }

        Expense expense = Expense.findById(id);
        if (expense == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Update fields
        expense.category = expenseUpdate.category;
        expense.description = expenseUpdate.description;
        expense.amount = expenseUpdate.amount;
        expense.gstApplicable = expenseUpdate.gstApplicable;
        expense.date = expenseUpdate.date;
        expense.attachmentUrl = expenseUpdate.attachmentUrl;

        return Response.ok(expense).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        boolean deleted = Expense.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}