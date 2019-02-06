package com.worldpay.offermanager.exception;

import com.worldpay.offermanager.model.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class OfferNotFoundExceptionMapper implements ExceptionMapper<OfferNotFoundException> {

	@Override
	public Response toResponse(OfferNotFoundException ex) {
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), 404);
		return Response.status(Status.NOT_FOUND)
				.entity(errorMessage)
				.build();
	}

}
