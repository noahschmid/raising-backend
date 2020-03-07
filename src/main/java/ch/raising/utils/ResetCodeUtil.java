package ch.raising.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.raising.data.ResetCodeRepository;
import ch.raising.models.Account;
import ch.raising.models.PasswordResetRequest;
import ch.raising.models.ResetCode;

@Service
public class ResetCodeUtil {
    @Autowired
    private static ResetCodeRepository resetCodeRepository;

    public ResetCodeUtil(ResetCodeRepository resetCodeRepository) {
        this.resetCodeRepository = resetCodeRepository;
    }

    private final static int CODE_LENGTH = 8;

    private static Random random = new Random();

    /**
     * Check if reset code for requested account matches with provided reset code
     * If code is expired, delete it. If 
     * @param accountId
     * @param request
     * @return true if request is valid, else false
     * @throws Exception
     */
    public boolean isValidRequest(int accountId, PasswordResetRequest request) throws Exception {
        List<ResetCode> codes = resetCodeRepository.findByCode(request.getCode());
        for(ResetCode code : codes) {
            if(code.getAccountId() == accountId) {
                resetCodeRepository.deleteByCode(code.getCode());
                return true;
            }

            if(isExpired(code))
                resetCodeRepository.deleteByCode(code.getCode());
        }

        codes = resetCodeRepository.findByAccountId(accountId);
        for(ResetCode code : codes) {
            resetCodeRepository.decrementAttempsLeft(code);
        }
        return false;
    }

    /**
     * Checks if there are no attemps left or code is expired
     * @param code the code to check
     * @return
     */
    public boolean isExpired(ResetCode code) {
        return (code.getExpiresAt().before(new Date()) || code.getAttempsLeft() <= 0); 
    }

    /**
     * Creates reset code and saves it in database for given accounts
     * @param accounts list of accounts to create code for
     * @return code as string
     */
    public String createResetCode(List<Account> accounts) {
        String code;
        do {
            code = generate();
        } while (resetCodeRepository.findByCode(code).size() > 0);

        Timestamp expiresAt = new Timestamp(new Date().getTime() + 60*1000*10);
        try{
            for(Account acc : accounts) {
                ResetCode resetCode = new ResetCode(code, (int)acc.getId(), expiresAt);
                resetCodeRepository.deleteByAccountId((int)acc.getId());
                resetCodeRepository.add(resetCode);
            }
        } catch(Exception e) {
            return null;
        }

        return code;
    }

    /**
     * Generates string of CODE_LENGTH random digits
     * @return code
     */
    private String generate() {
        String code = "";

        for(int i = 0; i < CODE_LENGTH; ++i) {
            code += random.nextInt(10);
        }

        return code;
    }
}