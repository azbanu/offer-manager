package com.worldpay.offermanager.service;

import com.worldpay.offermanager.exception.OfferNotFoundException;
import com.worldpay.offermanager.exception.ValidationException;
import com.worldpay.offermanager.model.Offer;

import java.util.List;

public interface OfferService {

    /**
     * This method will find an offer by id .
     *
     * @param offerId The id of the offer.
     * @return The offer if it exists
     * @exception OfferNotFoundException if offer with the given id does not exist.
     * @see RuntimeException
     */
    Offer getOfferById(Long offerId) ;

    /**
     * This method will add a given valid offer to the database.
     *
     * @param offer The offer to persist.
     * @return The offer if successfully saved.
     * @exception ValidationException if any of the parameter constraints is violated .
     * @see RuntimeException
     */
    Offer addOffer(Offer offer) ;

    /**
     * This method will cancel an active offer by setting the expiry date to now and status to CANCELLED.
     *
     * @param offerId The offer to cancel.
     * @return Nothing.
     */
    Offer cancelOffer(Long offerId) ;

    List<Offer> getOfferByDescription(String description);


    /**
     * This method will return the list of offer that matche the description.
     *
     * @return The list of offer that matches the description.
     */
    List<Offer> getOffers();

}
