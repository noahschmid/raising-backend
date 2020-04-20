package ch.raising.raisingbackend.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.raising.models.Interaction;
import ch.raising.models.RelationshipState;

class RelationshipStateTest {

	@Test
	void test() {
		String match = "MATCH";
		RelationshipState matchEnum = Enum.valueOf(RelationshipState.class, match);
		assertEquals(RelationshipState.MATCH, matchEnum);
		
		
		assertEquals(RelationshipState.valueOf("HANDSHAKE"), RelationshipState.HANDSHAKE);
		
		assertEquals(RelationshipState.valueOf("handshake".toUpperCase()), RelationshipState.HANDSHAKE);
		
	}
	@Test
	void make() {
		List<Integer> test = new ArrayList<Integer>();
		test.add(2);
		trythat(test);
		assertTrue(test.contains(99));
	}
	public void trythat(List<Integer> test) {
		test.add(99);
	}
}
