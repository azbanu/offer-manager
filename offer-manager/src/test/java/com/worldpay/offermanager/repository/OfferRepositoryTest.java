package com.worldpay.offermanager.repository;

import com.worldpay.offermanager.model.Offer;
import com.worldpay.offermanager.model.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OfferRepositoryTest {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void givenOffersByDescription_thenReturnOfferThatMatch() {
        // given
        Offer offerOne = getOfferObject("one", "offer one", LocalDateTime.now().plusHours(1));
        Offer offerTwo = getOfferObject("two", "offer two", LocalDateTime.now());
        Offer offerThree = getOfferObject("three", "offer three", LocalDateTime.now().minusHours(1));

        // when
        entityManager.persist(offerOne);
        entityManager.persist(offerTwo);
        entityManager.persist(offerThree);
        entityManager.flush();

        // then
        assertEquals(1, offerRepository.findOffersByDescription("offer one").size());
    }

    @Test
    public void givenOffersWithNoMatchingDescription_thenReturnNoOffer() {
        // given
        Offer offerOne = getOfferObject("one", "offer one", LocalDateTime.now().plusHours(1));
        Offer offerTwo = getOfferObject("two", "offer two", LocalDateTime.now());
        Offer offerThree = getOfferObject("three", "offer three", LocalDateTime.now().minusHours(1));

        // when
        entityManager.persist(offerOne);
        entityManager.persist(offerTwo);
        entityManager.persist(offerThree);
        entityManager.flush();

        // then
        assertEquals(0, offerRepository.findOffersByDescription("offer one2").size());
    }


    @Test
    public void givenNoOffers_thenReturnNoOffer() {
        // then
        assertEquals(0, offerRepository.findOffersByDescription("offer one").size());
    }

    @Test
    public void givenNull_thenReturnNoOffer() {
        // then
        assertEquals(0, offerRepository.findOffersByDescription(null).size());
    }

    @Test
    public void giveExpiredOffers_thenReturnCount() {
        // given
        Offer offerOne = getOfferObject("one", "offer one", LocalDateTime.now().minusHours(1));
        Offer offerTwo = getOfferObject("two", "offer two", LocalDateTime.now());
        Offer offerThree = getOfferObject("three", "offer three", LocalDateTime.now().minusHours(1));

        // when
        entityManager.persist(offerOne);
        entityManager.persist(offerTwo);
        entityManager.persist(offerThree);
        entityManager.flush();
        // then
        assertEquals(3l, offerRepository.countExpiredOffers(LocalDateTime.now()).longValue());
    }

    @Test
    public void giveNoExpiredOffers_thenReturnZeroCount() {
        // given
        Offer offerOne = getOfferObject("one", "offer one", LocalDateTime.now().plusHours(1));
        Offer offerTwo = getOfferObject("two", "offer two", LocalDateTime.now().plusHours(1));
        Offer offerThree = getOfferObject("three", "offer three", LocalDateTime.now().plusHours(1));

        // when
        entityManager.persist(offerOne);
        entityManager.persist(offerTwo);
        entityManager.persist(offerThree);
        entityManager.flush();

        // then
        assertEquals(0l, offerRepository.countExpiredOffers(LocalDateTime.now()).longValue());

    }

    @Test
    public void giveBothExpiredAndNonOffers_thenReturnCount() {
        // given
        Offer offerOne = getOfferObject("one", "offer one", LocalDateTime.now().minusHours(1));
        Offer offerTwo = getOfferObject("two", "offer two", LocalDateTime.now().plusHours(1));
        Offer offerThree = getOfferObject("three", "offer three", LocalDateTime.now().plusHours(1));

        // when
        entityManager.persist(offerOne);
        entityManager.persist(offerTwo);
        entityManager.persist(offerThree);
        entityManager.flush();

        // then
        assertEquals(01, offerRepository.countExpiredOffers(LocalDateTime.now()).longValue());

    }


    private Offer getOfferObject(String name, String description, LocalDateTime expiry) {
        Offer offer = new Offer();
        offer.setExpiryDate(expiry);
        offer.setCurrency(Currency.getInstance(Locale.UK));
        offer.setDescription(description);
        offer.setName(name);
        offer.setStatus(Status.ACTIVE);
        offer.setPrice(BigDecimal.TEN);
        return offer;
    }
}