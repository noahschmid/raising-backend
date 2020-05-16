package ch.raising.test.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.raising.services.StartupService;
import ch.raising.utils.UidUtil;

class UIdValidationAndFormatTest {

	@Test
	void testValidationValidNoPoints() {
		String uId = "CHE-109322551";
		assertEquals(true, UidUtil.isValidUId(uId));
	}
	@Test
	void testValid2() {
		String uId = "CHE-109.994.934";
		assertEquals(true, UidUtil.isValidUId(uId));
	}
	@Test
	void testValid3() {
		String uId = "BHZ-209.695.389";
		assertEquals(true, UidUtil.isValidUId(uId));
	}
	@Test
	void testValid4() {
		String uId = "109322551";
		assertEquals(true, UidUtil.isValidUId(uId));
	}


}
