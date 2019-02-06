package com.worldpay.offermanager.config;

import com.worldpay.offermanager.controller.OfferEndPoint;
import com.worldpay.offermanager.exception.GenericExceptionMapper;
import com.worldpay.offermanager.exception.OfferNotFoundExceptionMapper;
import com.worldpay.offermanager.exception.ValidationExceptionMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
public class JerseyConfigTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private JerseyConfig jerseyConfig = new JerseyConfig();
	
	@Before
	public void setUp() {
	}

	@Test
	public void createJerseyConfigWithValidArgumentsShouldReturnRegisteredClasses() {
		Set<Class<?>> result = jerseyConfig.getClasses();
		assertThat(result, notNullValue());
		assertThat(result.size(), equalTo(4));
		assertThat(result.contains(OfferEndPoint.class), equalTo(true));
		assertThat(result.contains(OfferNotFoundExceptionMapper.class), equalTo(true));
		assertThat(result.contains(ValidationExceptionMapper.class), equalTo(true));
		assertThat(result.contains(GenericExceptionMapper.class), equalTo(true));
	}

}
