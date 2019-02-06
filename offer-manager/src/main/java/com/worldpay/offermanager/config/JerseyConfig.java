package com.worldpay.offermanager.config;

import com.worldpay.offermanager.controller.OfferEndPoint;
import com.worldpay.offermanager.exception.GenericExceptionMapper;
import com.worldpay.offermanager.exception.OfferNotFoundExceptionMapper;
import com.worldpay.offermanager.exception.ValidationExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@Configuration
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {register(OfferEndPoint.class);
	register(OfferNotFoundExceptionMapper.class);
		register(ValidationExceptionMapper.class);
		register(GenericExceptionMapper.class);

	}
}
