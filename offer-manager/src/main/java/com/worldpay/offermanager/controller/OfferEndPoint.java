package com.worldpay.offermanager.controller;
import com.worldpay.offermanager.model.Offer;
import com.worldpay.offermanager.service.OfferService;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Component
@Path("/offer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OfferEndPoint {

	private final Logger logger = LoggerFactory.getLogger(OfferEndPoint.class);

	@Autowired
    OfferService offerService;

	@GET
	@Path("/list")
	public List<Offer> getAllOffer(@QueryParam("description") String description) {
		logger.info("Listing offers by {}", description);
		if (Strings.isEmpty(description))
		   return offerService.getOffers();
		return offerService.getOfferByDescription(description);
	}




	@POST
	public Response addOffer(Offer offer, @Context UriInfo uriInfo) {
		logger.info("Adding offer {0}", offer.toString());
		Offer newOffer = offerService.addOffer(offer);
		String newId = String.valueOf(newOffer.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri)
				.entity(newOffer)
				.build();
	}


	@PUT
	@Path("/cancel/{offerId}")
	public Offer cancelOffer(@PathParam("offerId") long offerId) {
		logger.info("Cancelling  offer {}", offerId);
		Offer offer = offerService.cancelOffer(offerId);
		return offer;
	}

	@GET
	@Path("/{offerId}")
	public Offer getOfferById(@PathParam("offerId") long offerId) {
		logger.info("Get offer by Id: {}", offerId);
		return offerService.getOfferById(offerId);
	}
}
