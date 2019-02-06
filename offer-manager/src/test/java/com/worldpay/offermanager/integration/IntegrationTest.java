package com.worldpay.offermanager.integration;


import com.worldpay.offermanager.OfferManagerApplication;
import com.worldpay.offermanager.model.Offer;
//import com.worldpay.offermanager.model.Status;
import com.worldpay.offermanager.model.Status;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OfferManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class IntegrationTest  {

    @LocalServerPort
     int  port;

    private static Client jerseyClient;


    @Before
    public void setup() {
       ClientConfig jerseyClientConfig = new ClientConfig().register( JerseyClient.class );
       jerseyClient = ClientBuilder.newClient(jerseyClientConfig);

    }

    @After
    public void cleanup() {
        jerseyClient = null;

    }

    @Test
    public void listAllOffer_shouldReturnAllOffers() {
        Offer givenOffer = getOfferObject("two", "two offer", LocalDateTime.now().plusHours(1));
        postOffer(givenOffer);
        Response response = get0ffers();
        List<Offer> result = response.readEntity(new GenericType<List<Offer>>() {
        });

        MatcherAssert.assertThat(result, notNullValue());
        assertEquals(Response.Status.OK.getStatusCode()
                ,response.getStatus());
        assertTrue(result.size() > 0);

    }



    @Test
    public void getAllOfferWithDescriptionParameter_ShouldReturnAllMatchingOffers() {
        Offer givenOffer = getOfferObject("three", "three offer", LocalDateTime.now().plusHours(1));
        postOffer(givenOffer);
        Response response = get0ffersWithDescription(givenOffer.getDescription());
        List<Offer> result = response.readEntity(new GenericType<List<Offer>>() {
        });

        MatcherAssert.assertThat(result, notNullValue());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(1,result.size());
        assertEquals(result.get(0).getDescription(), givenOffer.getDescription());
    }

    @Test
    public void getAllOfferWithDescriptionParameter_WhenNoMatch_ShouldReturnEmptyList() {
        Response response = get0ffersWithDescription("no match");
        List<Offer> result = response.readEntity(new GenericType<List<Offer>>() {
        });

        MatcherAssert.assertThat(result, notNullValue());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        assertEquals(0,result.size());

    }


    @Test
    public void addOffer_shouldReturnResponse() {
        Offer givenOffer = getOfferObject("Four", "four hour offer", LocalDateTime.now().plusHours(1));
        Response response = postOffer(givenOffer);
        Offer result = response.readEntity(Offer.class);
        MatcherAssert.assertThat(result, notNullValue());
        assertEquals(Response.Status.CREATED.getStatusCode(),response.getStatus() );
        assertEquals(result.getDescription(), givenOffer.getDescription());
    }


    @Test
    public void addOffer_whenExpiryIsNull_shouldThrowException() {
        Offer givenOffer = getOfferObject("Four", "four hour offer", LocalDateTime.now().plusHours(1));
        givenOffer.setExpiryDate(null);
        Response response = postOffer(givenOffer);
        assertEquals (Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void addOffer_whenCurrencyIsNull_shouldThrowException() {
        Offer givenOffer = getOfferObject("Four", "four  offer", LocalDateTime.now().plusHours(1));
        givenOffer.setCurrency(null);

        Response response = postOffer(givenOffer);
        assertEquals (Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void addOffer_whenPriceIsNull_shouldThrowException() {
        Offer givenOffer = getOfferObject("Four", "four  offer", LocalDateTime.now().plusHours(1));
        givenOffer.setPrice(null);

        Response response = postOffer(givenOffer);
        assertEquals (Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void cancelOffer_whenOfferIsActive_shouldError() {
        Offer givenOffer = getOfferObject("Four", "Four  offer", LocalDateTime.now().plusHours(1));
        Response response = postOffer(givenOffer);
        Long id = response.readEntity(Offer.class).getId();

        Response result = cancelOffer(id);
        Offer cancelledOffer = result.readEntity(Offer.class);
        MatcherAssert.assertThat(result, notNullValue());
        assertEquals(200,result.getStatus()) ;
        assertEquals(Status.CANCELLED, cancelledOffer.getStatus()) ;
    }


    @Test
    public void cancelOffer_whenOfferHasExpire_shouldThrowException() throws InterruptedException {
        Offer givenOffer = getOfferObject("Four", "two hour offer", LocalDateTime.now().plusSeconds(2));
        Response response = postOffer(givenOffer);
        Long id = response.readEntity(Offer.class).getId();

        Thread.sleep(5000);
        Response result = cancelOffer(id);
        MatcherAssert.assertThat(result, notNullValue());
        assertEquals (Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus());

    }


    @Test
    public void getOfferById_shouldReturnOffer() {
        Offer givenOffer = getOfferObject("Four", "two hour offer", LocalDateTime.now().plusHours(2));
        Response response = postOffer(givenOffer);
        Long id = response.readEntity(Offer.class).getId();

        Response responseResult =  get0ffer(id);
        Offer offer =  responseResult.readEntity(Offer.class);

        MatcherAssert.assertThat(offer, notNullValue());
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatus());

        assertEquals( offer.getId(), id);
    }

    @Test
    public void getOfferById_whenNotFound_shouldThrowException() {
        Response result = cancelOffer(1l);
        MatcherAssert.assertThat(result, notNullValue());

        assertEquals (Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus());
    }




    public Response postOffer(Offer offer) {
        WebTarget webTarget = jerseyClient.target("http://localhost:"+ port+ "/api/offer/");
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(Entity.entity(offer, MediaType.APPLICATION_JSON));

        return response;
    }

    public Response get0ffer(Long offerId) {
        WebTarget webTarget = jerseyClient.target("http://localhost:"+ port+ "/api/offer/").path(offerId+"");
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        return response;
    }

    public Response get0ffers() {
        WebTarget webTarget = jerseyClient.target("http://localhost:"+ port+ "/api/offer/").path("list");
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get(Response.class);
        return response;
    }

    public Response get0ffersWithDescription(String description) {
        WebTarget webTarget = jerseyClient.target("http://localhost:"+ port+ "/api/offer/").path("list")
                .queryParam("description", description);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
       return response;
    }

    public Response cancelOffer(Long offerId) {
        WebTarget webTarget = jerseyClient.target("http://localhost:"+ port+ "/api/offer/").path("cancel").path(offerId+"");
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.put(Entity.entity(1l, MediaType.APPLICATION_JSON));
        return response;
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

