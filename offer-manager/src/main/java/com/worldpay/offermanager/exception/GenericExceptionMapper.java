package com.worldpay.offermanager.exception;

import com.worldpay.offermanager.model.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	private final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

	@Override
	public Response toResponse(Throwable ex) {

		logger.info("Internal error:  %s ", ex.getMessage());
		ErrorMessage errorMessage = new ErrorMessage("Sorry something went wrong", 500 );
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(errorMessage)
				.build();
	}

}