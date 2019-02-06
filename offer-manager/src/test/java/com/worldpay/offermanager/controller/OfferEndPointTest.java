package com.worldpay.offermanager.controller;


import com.worldpay.offermanager.exception.OfferNotFoundException;
import com.worldpay.offermanager.exception.ValidationException;
import com.worldpay.offermanager.model.Offer;
import com.worldpay.offermanager.service.OfferService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OfferEndPointTest  {

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferEndPoint offerEndPoint;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ArrayList<Offer> offers;
    private ArrayList<Offer> matchingOffers;
    private Offer offer;

    @Before
    public void setUp() {
        offers = new ArrayList();
        matchingOffers = new ArrayList();

        offers.add(getOfferObject("One", "One offer", LocalDateTime.now().plusHours(1)));
        offers.add(getOfferObject("Two", "Two offer", LocalDateTime.now().plusHours(1)));

        matchingOffers.add(getOfferObject("Three", "one hour offer", LocalDateTime.now().plusHours(1)));
        offer = getOfferObject("Four", "two hour offer", LocalDateTime.now().plusHours(1));
    }


    @Test
    public void listAllOffers_ShouldReturnAllOffers() {
        given(offerService.getOffers()).willReturn(offers);
        List<Offer> result = offerEndPoint.getAllOffer(null);
        assertThat(result, notNullValue());
        assertEquals(2,result.size());
        verify(offerService, times(1)).getOffers();
    }

    @Test
    public void listAllOffer_whenNoOffer_ShouldReturnEmptyList() {
        given(offerService.getOffers()).willReturn(Collections.emptyList());
        List<Offer> result = offerEndPoint.getAllOffer(null);
        assertThat(result, notNullValue());
        assertEquals(result.size(),0);
        verify(offerService, times(1)).getOffers();
    }

    @Test
    public void listAllOffers_withDescriptionParameter_ShouldReturnAllMatchingOffers() {
        given(offerService.getOfferByDescription("one hour offer")).willReturn(matchingOffers);
        List<Offer> result = offerEndPoint.getAllOffer("one hour offer");
        assertThat(result, notNullValue());
        assertEquals(1,result.size());
        verify(offerService, times(1)).getOfferByDescription("one hour offer");
    }

    @Test
    public void listAllOffer_withDescriptionParameter_WhenNoMatch_ShouldReturnEmptyList() {
        given(offerService.getOfferByDescription("ten hour offer")).willReturn(new ArrayList<>());
        List<Offer> result = offerEndPoint.getAllOffer("ten hour offer");
        assertEquals(0,result.size());
        verify(offerService, times(1)).getOfferByDescription("ten hour offer");
    }



    @Test
    public void addOffer_shouldReturnResponse() {
        Offer newOffer = getOfferObject("Four", "two hour offer", LocalDateTime.now().plusHours(1));
        newOffer.setId(1l);
        given(offerService.addOffer(offer)).willReturn(newOffer);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        UriBuilder  uriBuilder = Mockito.mock(UriBuilder.class);

        given(uriInfo.getAbsolutePathBuilder()).willReturn(uriBuilder);
        given(uriBuilder.path("1")).willReturn(uriBuilder);
        given(uriBuilder.build()).willReturn(URI.create("api/offer/list/1"));

        Response result = offerEndPoint.addOffer(offer, uriInfo);
        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(201));
        assertThat(result.getLocation().toString(), equalTo("api/offer/list/1"));
        verify(offerService, times(1)).addOffer(offer);
    }


    @Test(expected = ValidationException.class)
    public void addOffer_whenDescriptionIsNull_shouldThrowException() {
        offer.setDescription(null);
        given(offerService.addOffer(offer)).willThrow(new ValidationException("Offer description can not be empty"));
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        offerEndPoint.addOffer(offer, uriInfo);
    }

    @Test(expected = ValidationException.class)
    public void addOffer_whenExpiryIsNull_shouldThrowException() {
        offer.setExpiryDate(null);
        given(offerService.addOffer(offer)).willThrow(new ValidationException("Expiry date is not valid"));
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        offerEndPoint.addOffer(offer, uriInfo);
    }

    @Test(expected = ValidationException.class)
    public void addOffer_whenCurrencyIsNull_shouldThrowException() {
        offer.setCurrency(null);
        given(offerService.addOffer(offer)).willThrow(new ValidationException("Currency value can not be empty"));
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        offerEndPoint.addOffer(offer, uriInfo);
    }

    @Test(expected = ValidationException.class)
    public void addOffer_whenPriceIsNull_shouldThrowException() {
        offer.setPrice(null);
        given(offerService.addOffer(offer)).willThrow(new ValidationException("Offer price can not be empty"));
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        offerEndPoint.addOffer(offer, uriInfo);
    }

    @Test
    public void cancelOffer_whenOfferIsActive_shouldReturnTheCancelledOffer() {
        Offer givenOffer = getOfferObject("Four", "two hour offer", LocalDateTime.now().plusHours(1));
        givenOffer.setId(1l);
        given(offerService.cancelOffer(givenOffer.getId())).willReturn(givenOffer);
        offerEndPoint.cancelOffer(1l);
        verify(offerService, times(1)).cancelOffer(1l);
    }

    @Test(expected = ValidationException.class)
    public void cancelOffer_whenOfferHasExpire_shouldThrowException() {
        Mockito.doThrow(new ValidationException("Offer Id should not be null")).when(offerService).cancelOffer(1l);
        offerEndPoint.cancelOffer(1l);
        verify(offerService, times(1)).cancelOffer(1l);
    }


    @Test
    public void getOfferById_shouldReturnOffer() {
        Offer givenOffer = getOfferObject("Four", "two hour offer", LocalDateTime.now().plusHours(1));
        givenOffer.setId(1l);
        given(offerService.getOfferById(1l)).willReturn(givenOffer);
        Offer result = offerEndPoint.getOfferById(1l);
        verify(offerService, times(1)).getOfferById(1l);
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(1l));
    }

    @Test(expected = OfferNotFoundException.class)
    public void getOfferById_whenNotFound_shouldThrowException() {
        Mockito.doThrow(new OfferNotFoundException("Offer with id 1 not found")).when(offerService).getOfferById(1l);
        offerEndPoint.getOfferById(1l);
        verify(offerService, times(1)).getOfferById(1l);
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

