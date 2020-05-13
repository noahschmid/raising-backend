package ch.raising.test.service;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;

import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.MatchingProfile;
import ch.raising.services.MatchingService;

class MatchingServiceTest {
    @Test
    void testEquals() {
    	AssignmentTableModel ind1 = new AssignmentTableModel("test", 1);
    	AssignmentTableModel ind2 = new AssignmentTableModel("test2", 2);

        boolean result = ind1.equals(ind2);
        assertEquals(false, result, "Industries don't match");

        result = ind1.equals(null);
        assertEquals(false, result, "Industries don't match");

        result = ind1.equals(ind1);
        assertEquals(true, result, "Industries match");

        result = ind1.equals(new AssignmentTableModel("test", 1));
        assertEquals(true, result, "Industries match");
    }

    @Test
    void testContains() {
        ArrayList<Country> countries = new ArrayList<>();
        countries.add(new Country("Switzerland", 1, 1));
        boolean result = countries.contains(new Country("Germany", 2, 2));
        assertEquals(false, result, "Country list doesn't contain Germany");
    }

    @Test
    void testInvestmentRange() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        // subject investment range inside object range
        subject.setInvestmentMax(500);
        subject.setInvestmentMin(300);
        object.setInvestmentMax(1000);
        object.setInvestmentMin(100);
        int score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Subject range inside object range");
        score = MatchingService.getMatchingScore(object, subject);
        assertEquals(1, score, "Subject range inside object range");

        // object investment range inside subject range
        object.setInvestmentMax(500);
        object.setInvestmentMin(300);
        subject.setInvestmentMax(1000);
        subject.setInvestmentMin(100);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Object range inside subject range");
        score = MatchingService.getMatchingScore(object, subject);
        assertEquals(1, score, "Object range inside subject range");

        // ranges intersecting
        subject.setInvestmentMax(1000);
        subject.setInvestmentMin(400);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Ranges intersecting");
        score = MatchingService.getMatchingScore(object, subject);
        assertEquals(1, score, "Ranges intersecting");

        subject.setInvestmentMax(400);
        subject.setInvestmentMin(100);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Ranges intersecting 2");
        score = MatchingService.getMatchingScore(object, subject);
        assertEquals(1, score, "Ranges intersecting 2");

        // not intersecting
        subject.setInvestmentMax(200);
        subject.setInvestmentMin(100);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "Ranges not intersecting");
        score = MatchingService.getMatchingScore(object, subject);
        assertEquals(0, score, "Ranges not intersecting");

        // same range
        object.setInvestmentMax(200);
        object.setInvestmentMin(100);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Same range");
    }

    @Test
    void testCountryMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        int score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No countries match 0");
        subject.addCountry(new Country("Switzerland", 1, 1));
        object.addCountry(new Country("Germany", 2, 2));
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No countries match 1");

        object.addCountry(new Country("Switzerland", 1,1 ));
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Countries match");

        subject.addCountry(new Country("Germany", 2,1 ));
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Multiple countries match");
    }


    @Test
    void testContinentCountryMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        int score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No countries match 0");
        subject.addCountry(new Country("Switzerland", 1, 1));
        object.addContinent(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No countries match 1");

        object.addContinent(1l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Countries match");
    }

    @Test
    void testContinentCountryMatch2() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        int score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No countries match 0");
        object.addCountry(new Country("Switzerland", 1, 1));
        subject.addContinent(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No countries match 1");

        subject.addContinent(1l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Countries match");
    }

    @Test
    void testInvestmentPhasesMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        int score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No investment phases match");
        subject.addInvestmentPhase(1l);
        object.addInvestmentPhase(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No investment phases match");

        object.addInvestmentPhase(1l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Investment phases match");

        subject.addCountry(new Country("Round B", 2, 1));
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Multiple investment phases match");
    }

    @Test
    void testIndustriesMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        // subject investment range inside object range
        int score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No industries match");
        subject.addIndustry(1l);
        object.addIndustry(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No industries match");

        object.addIndustry(1l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Industries match");

        subject.addIndustry(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Multiple industries match");
    }

    @Test
    void testInvestorTypeMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        // subject investment range inside object range
        int score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No investor types match");
        subject.addInvestorType(1l);
        object.addInvestorType(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No investor types match");

        object.addInvestorType(1l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "investor types match");

        subject.addInvestorType(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Multiple investor types match");
    }

    @Test
    void testSupportMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        // subject investment range inside object range
        int score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No supports match");
        subject.addSupport(1l);
        object.addSupport(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(0, score, "No supports match");

        object.addSupport(1l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "supportsmatch");

        subject.addSupport(2l);
        score = MatchingService.getMatchingScore(subject, object);
        assertEquals(1, score, "Multiple supports match");
    }
}