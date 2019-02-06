package com.worldpay.offermanager.exception;

import com.worldpay.offermanager.model.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    private final Logger logger = LoggerFactory.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(ValidationException ex) {

        logger.info("Internal error:  %s ", ex.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), 500 );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorMessage)
                .build();
    }

}