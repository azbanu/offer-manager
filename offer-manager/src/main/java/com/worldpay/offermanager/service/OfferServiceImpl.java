package com.worldpay.offermanager.service;

import com.worldpay.offermanager.exception.OfferNotFoundException;
import com.worldpay.offermanager.exception.ValidationException;
import com.worldpay.offermanager.model.Offer;
import com.worldpay.offermanager.model.Status;
import com.worldpay.offermanager.repository.OfferRepository;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OfferServiceImpl implements OfferService {

    private final Logger logger = LoggerFactory.getLogger(OfferServiceImpl.class);

    @Autowired
    private OfferRepository offerRepository;

    /**
     * This method will find an offer by id .
     *
     * @param offerId The id of the offer.
     * @return The offer if it exists
     * @exception OfferNotFoundException if offer with the given id does not exist.
     * @see RuntimeException
     */
    public Offer getOfferById(Long offerId) {
        Objects.requireNonNull(offerId, "Offer Id should not be null");

     logger.info("Get offer by id  {} ", offerId);
     Optional<Offer> offer = offerRepository.findById(offerId);

     if (offer.isPresent()) {
         Offer offerPresent = offer.get();
         if (offerPresent.getExpiryDate().isBefore(LocalDateTime.now()) && Status.ACTIVE.equals(offerPresent.getStatus())) {
             // no need to save the Scheduler will catchup
             offerPresent.setStatus(Status.EXPIRED);

           }
         return offerPresent;
     }else
          throw new OfferNotFoundException("Offer with id " + offerId + " not found");
    }


    /**
     * This method will add a given valid offer to the database.
     *
     * @param offer The offer to persist.
     * @return The offer if successfully saved.
     * @exception ValidationException if any of the parameter constraints is violated .
     * @see RuntimeException
     */
    public Offer addOffer(Offer offer) {
        logger.info("Adding offer {} ", offer.toString());
        Objects.requireNonNull(offer, "Offer Id should not be null");
        validateOffer(offer);
        offer.setStatus(Status.ACTIVE);
        return offerRepository.save(offer);
    }

    /**
     * This method will cancel an active offer by setting the expiry date to now and status to CANCELLED.
     *
     * @param offerId The offer to cancel.
     * @return Nothing.
     */
    @Override
    public Offer  cancelOffer(Long offerId) {
        Objects.requireNonNull(offerId, "Offer Id should not be null");
        logger.info("Cancelling offer {} ", offerId);
        Offer offer = getOfferById(offerId);

        if (Status.EXPIRED.equals(offer.getStatus()))
            throw new ValidationException("Offer could not be cancelled because it has expired");

        if (Status.CANCELLED.equals(offer.getStatus()))
            throw new ValidationException("Offer could not be cancelled because it has already been cancelled");

        offer.setExpiryDate(LocalDateTime.now());
        offer.setStatus(Status.CANCELLED);
        return offerRepository.save(offer);

    }

    /**
     * This method will return all offers that match a given description.
     *
     * @param description The description to search for.
     * @return The list of offer that matches the description.
     */
    @Override
    public List<Offer> getOfferByDescription(String description) {
        logger.info("Get offers by description");
        if (Strings.isBlank(description))
            return Collections.emptyList();

        return updateStatusOfExpiredButStillActiveOffers(offerRepository.findOffersByDescription(description));
    }

    /**
     * This method will return the list of offer that matche the description.
     *
     * @return The list of offer that matches the description.
     */
    @Override
    public List<Offer> getOffers() {
       logger.info("Get all offers");
       return updateStatusOfExpiredButStillActiveOffers(offerRepository.findAll());
    }

    /**
     * This method will set the status of expired offer every 50 seconds
     *
     */
    @Scheduled(cron = "0/10 * * * * ?")
    private void refreshOfferStatus() {
        logger.info("Refreshing offer statuses");
        if (offerRepository.countExpiredOffers(LocalDateTime.now()) > 0)
         offerRepository.updateExpiryStatus(LocalDateTime.now());
    }

    /**
     * Make up for the real-time expiry
     * This method check and update the status of offers that has expired since the last scheduled run
     * Note: the save method is not called this will be done by the next scheduler run
     *
     */
    private List<Offer> updateStatusOfExpiredButStillActiveOffers(List<Offer> offers)
    {
        for (Offer offer : offers)
        {
            if (offer.getExpiryDate().isBefore(LocalDateTime.now()) && Status.ACTIVE.equals(offer.getStatus()))
                offer.setStatus(Status.EXPIRED);
        }

        return offers;
    }

    /**
     * This method will validate the constraint on an offer.
     *
     * @return Nothing
     * @exception ValidationException if any of the parameter constraints is violated .
     */
    private void validateOffer(Offer offer)
    {
        if (offer.getCurrency() == null)
            throw new ValidationException("Currency value can not be empty");

        if (StringUtils.isEmpty(offer.getDescription()))
            throw new ValidationException("Offer description can not be empty");

        if (StringUtils.isEmpty(offer.getName()))
            throw new ValidationException("Offer name can not be empty");

        if (offer.getPrice() == null)
            throw new ValidationException("Offer price can not be empty");

        if (offer.getExpiryDate() == null || offer.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new ValidationException("Expiry date is not valid");

    }
}
