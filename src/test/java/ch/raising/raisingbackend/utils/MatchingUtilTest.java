package ch.raising.raisingbackend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import ch.raising.config.AppConfig;
import ch.raising.controllers.AccountController;
import ch.raising.models.Country;
import ch.raising.models.Industry;
import ch.raising.models.InvestmentPhase;
import ch.raising.models.LoginRequest;
import ch.raising.models.MatchingProfile;
import ch.raising.services.AccountService;
import ch.raising.utils.MatchingUtil;

@SpringBootTest
class MatchingUtilTest {
    @Test
    void testEquals() {
        Industry ind1 = new Industry(1, "test");
        Industry ind2 = new Industry(2, "test2");

        boolean result = ind1.equals(ind2);
        assertEquals(false, result, "Industries don't match");

        result = ind1.equals(null);
        assertEquals(false, result, "Industries don't match");

        result = ind1.equals(ind1);
        assertEquals(true, result, "Industries match");

        result = ind1.equals(new Industry(1, "test"));
        assertEquals(true, result, "Industries match");
    }

    @Test
    void testContains() {
        ArrayList<Country> countries = new ArrayList<>();
        countries.add(new Country(1, "Switzerland"));
        boolean result = countries.contains(new Country(2, "Germany"));
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
        int score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Subject range inside object range");

        // object investment range inside subject range
        object.setInvestmentMax(500);
        object.setInvestmentMin(300);
        subject.setInvestmentMax(1000);
        subject.setInvestmentMin(100);
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Object range inside subject range");

        // ranges intersecting
        subject.setInvestmentMax(1000);
        subject.setInvestmentMin(400);
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Ranges intersecting");

        subject.setInvestmentMax(400);
        subject.setInvestmentMin(100);
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Ranges intersecting 2");

        // not intersecting
        subject.setInvestmentMax(200);
        subject.setInvestmentMin(100);
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(0, score, "Ranges not intersecting");
    }

    @Test
    void testCountryMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        // subject investment range inside object range
        int score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(0, score, "No countries match 0");
        subject.addCountry(new Country(1, "Switzerland"));
        object.addCountry(new Country(2, "Germany"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(0, score, "No countries match 1");

        object.addCountry(new Country(1, "Switzerland"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Countries match");

        subject.addCountry(new Country(2, "Germany"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Multiple countries match");
    }

    @Test
    void testInvestmentPhasesMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        int score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(0, score, "No investment phases match");
        subject.addInvestmentPhase(new InvestmentPhase(1, "Round A"));
        object.addInvestmentPhase(new InvestmentPhase(2, "Round B"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(0, score, "No investment phases match");

        object.addInvestmentPhase(new InvestmentPhase(1, "Round A"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Investment phases match");

        subject.addCountry(new Country(2, "Round B"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Multiple investment phases match");
    }

    @Test
    void testIndustriesMatch() throws Exception {
        MatchingProfile subject = new MatchingProfile();
        MatchingProfile object = new MatchingProfile();

        // subject investment range inside object range
        int score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(0, score, "No industries match");
        subject.addIndustry(new Industry(1, "Tech"));
        object.addIndustry(new Industry(2, "Health"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(0, score, "No industries match");

        object.addIndustry(new Industry(1, "Tech"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Industries match");

        subject.addIndustry(new Industry(2, "Health"));
        score = MatchingUtil.getMatchingScore(subject, object);
        assertEquals(1, score, "Multiple industries match");
    }
}