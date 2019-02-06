package com.worldpay.offermanager.service;

import com.worldpay.offermanager.exception.OfferNotFoundException;
import com.worldpay.offermanager.exception.ValidationException;
import com.worldpay.offermanager.model.Offer;
import com.worldpay.offermanager.model.Status;
import com.worldpay.offermanager.repository.OfferRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferServiceImpl offerService;


    @Test
    public void getOfferById() {
        Offer givenOffer = getOfferObject("One","Offer one",LocalDateTime.now().plusHours(1));
        given(offerRepository.findById(1l)).willReturn(Optional.of(givenOffer));

        Offer offer = offerService.getOfferById(1l);
        assertNotNull(offer);
        verify(offerRepository, times(1)).findById(1l);

    }

    @Test
    public void getOfferById_whenOfferExpired_thenReturnExpiredOffer()
    {
        Offer givenOffer = getOfferObject("One","Offer one",LocalDateTime.now().minusMinutes(40));
        given(offerRepository.findById(1l)).willReturn(Optional.of(givenOffer));
        Offer result = offerService.getOfferById(1l);
        assertNotNull(result);
        assertEquals(Status.EXPIRED, result.getStatus());
        verify(offerRepository, times(1)).findById(1l);
    }

    @Test(expected = OfferNotFoundException.class)
    public void getOfferById_whenOfferDoesNotExist_thenExceptionIsThrown() {
        given(offerRepository.findById(2l)).willReturn(Optional.<Offer>empty());
        offerService.getOfferById(2l);
    }



    @Test
    public void addOffer_thenOfferSaved()
    {
        Offer givenOffer = getOfferObject("One","Offer one",LocalDateTime.now().plusHours(1));
        given(offerRepository.save(givenOffer)).willReturn(givenOffer);
        Offer returnedOffer = offerService.addOffer(givenOffer);
        assertNotNull(returnedOffer);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }


    @Test(expected = ValidationException.class)
    public void addInValidNameOffer_thenExceptionThrown()
    {
        Offer givenOffer = getOfferObject(null,"Offer one",LocalDateTime.now().plusHours(1));
        Offer returnedOffer = offerService.addOffer(givenOffer);
        assertNotNull(returnedOffer);
    }

    @Test(expected = ValidationException.class)
    public void addInValidDescriptionOffer_thenExceptionThrown()
    {
        Offer givenOffer = getOfferObject("Offer",null,LocalDateTime.now().plusHours(1));
        Offer returnedOffer = offerService.addOffer(givenOffer);
        assertNotNull(returnedOffer);
    }

    @Test(expected = ValidationException.class)
    public void addInValidPriceOffer_thenExceptionThrown()
    {
        Offer givenOffer = getOfferObject("One","Offer one",LocalDateTime.now().plusHours(1));
        givenOffer.setPrice(null);
        Offer returnedOffer = offerService.addOffer(givenOffer);
        assertNotNull(returnedOffer);
    }

    @Test(expected = ValidationException.class)
    public void addInValidCurrencyOffer_thenExceptionThrown()
    {
        Offer givenOffer = getOfferObject(null,"Offer one",LocalDateTime.now().plusHours(1));
        givenOffer.setCurrency(null);
        Offer returnedOffer = offerService.addOffer(givenOffer);
        assertNotNull(returnedOffer);
    }

    @Test(expected = ValidationException.class)
    public void addExpiredOffer_thenExceptionThrown()
    {
        Offer givenOffer = getOfferObject(null,"Offer one",LocalDateTime.now().minusHours(1));
        Offer returnedOffer = offerService.addOffer(givenOffer);
        assertNotNull(returnedOffer);
    }


    @Test
    public void cancelOffer_thenChangeExpiryDate() {
        LocalDateTime futureExpiryDate =LocalDateTime.now().plusHours(1);
        Offer givenOffer = getOfferObject("One","Offer one", futureExpiryDate);
        given(offerRepository.findById(1l)).willReturn(Optional.of(givenOffer));
        offerService.cancelOffer(1l);

        verify(offerRepository, times(1)).save(any(Offer.class));
        assertNotEquals(futureExpiryDate, givenOffer.getExpiryDate());
    }



    @Test(expected = OfferNotFoundException.class)
    public void cancelInValidOffer_thenThrowException() {
        given(offerRepository.findById(1l)).willReturn(Optional.<Offer>empty());
        offerService.cancelOffer(1l);
    }

    @Test(expected = ValidationException.class)
    public void cancelExpiredOffer_thenThrowException() {
        Offer givenOffer = getOfferObject(null,"Offer one",LocalDateTime.now().minusHours(1));
        given(offerRepository.findById(1l)).willReturn(Optional.of(givenOffer));
        offerService.cancelOffer(1l);
    }

    @Test(expected = ValidationException.class)
    public void cancelAlreadyCanceledOffer_thenThrowException() {
        Offer givenOffer = getOfferObject(null,"Offer one",LocalDateTime.now().minusHours(1));
        givenOffer.setStatus(Status.CANCELLED);
        given(offerRepository.findById(1l)).willReturn(Optional.of(givenOffer));
        offerService.cancelOffer(1l);
    }


    @Test
    public void getOffers() {
        ArrayList<Offer> offers = new ArrayList<>();
        Offer givenOffer1 = getOfferObject("One","Offer one",LocalDateTime.now().plusHours(1));
        Offer givenOffer2 = getOfferObject("Two","Offer two",LocalDateTime.now().plusHours(1));
        offers.add(givenOffer1);
        offers.add(givenOffer2);
        given(offerRepository.findAll()).willReturn(offers);
        List<Offer> result = offerService.getOffers();

        verify(offerRepository, times(1)).findAll();
        assertEquals(2, result.size());
    }

    @Test
    public void getOffers_whenNonExist() {
        ArrayList<Offer> offers = new ArrayList<>();
        given(offerRepository.findAll()).willReturn(offers);
        List<Offer> result = offerService.getOffers();

        verify(offerRepository, times(1)).findAll();
        assertEquals(0, result.size());
    }


    @Test
    public void findOffersByDescription() {
        ArrayList<Offer> offers = new ArrayList<>();
        Offer givenOffer1 = getOfferObject("One","Offer one",LocalDateTime.now().plusHours(1));
        Offer givenOffer2 = getOfferObject("Two","Offer one",LocalDateTime.now().plusHours(1));
        offers.add(givenOffer1);
        offers.add(givenOffer2);
        given(offerRepository.findOffersByDescription(eq("Offer one"))).willReturn(offers);
        List<Offer> result = offerService.getOfferByDescription("Offer one");
        verify(offerRepository, times(1)).findOffersByDescription(eq("Offer one"));
        assertEquals(2, result.size());
    }


    @Test
    public void findOffers_whenNoMatch() {
        ArrayList<Offer> offers = new ArrayList<>();

        given(offerRepository.findOffersByDescription(eq("Offer one"))).willReturn(offers);
        List<Offer> result = offerService.getOfferByDescription("Offer one");
        verify(offerRepository, times(1)).findOffersByDescription(eq("Offer one"));
        assertEquals(0, result.size());
    }


    private Offer getOfferObject(String name, String description, LocalDateTime expiry) {
        Offer offer = new Offer();
        offer.setExpiryDate(expiry);
        offer.setCurrency(Currency.getInstance(Locale.UK));
        offer.setDescription(description);
        offer.setName(name);
        offer.setPrice(BigDecimal.TEN);
        return offer;
    }
}