package ch.raising.utils;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import ch.raising.data.ResetCodeRepository;
import ch.raising.models.Account;
import ch.raising.models.PasswordResetRequest;
import ch.raising.models.ResetCode;

@Service
public class ResetCodeUtil {
    
    private static ResetCodeRepository resetCodeRepository;

    @Autowired
    public ResetCodeUtil(ResetCodeRepository resetCodeRepository) {
        this.resetCodeRepository = resetCodeRepository;
    }

    private final static int CODE_LENGTH = 8;

    private static Random random = new Random();

    /**
     * Check if reset code for requested account matches with provided reset code
     * If code is expired, delete it. If 
     * @param request instance of password reset request
     * @return -1 if request isnt valid, else accountId of account the request belongs to 
     * @throws PasswordResetException 
     * @throws Exception
     */
    public long validate(PasswordResetRequest request) throws SQLException, DataAccessException, PasswordResetException {
        ResetCode code = resetCodeRepository.findByCode(request.getCode());
        if(code == null)
        	throw new PasswordResetException("Resetcode is Invalid");

        resetCodeRepository.deleteByCode(code.getCode());

        if(isExpired(code)) 
        	throw new PasswordResetException("Code is Expired");

        return code.getAccountId();
    }

    /**
     * Checks if there are no attemps left or code is expired
     * @param code the code to check
     * @return
     */
    public boolean isExpired(ResetCode code) {
        return code.getExpiresAt().before(new Date()); 
    }

    /**
     * Creates reset code and saves it in database for given accounts
     * @param account account to create code for
     * @return code as string
     */
    public String createResetCode(Account account) {
        if(account == null)
            return null;

        String code;
        resetCodeRepository.deleteByAccountId(account.getAccountId());

        do {
            code = generate();
        } while (resetCodeRepository.findByCode(code) != null);

        Timestamp expiresAt = new Timestamp(new Date().getTime() + 60*1000*10);
        try{
                ResetCode resetCode = new ResetCode(code, (int)account.getAccountId(), expiresAt);
                resetCodeRepository.deleteByAccountId((int)account.getAccountId());
                resetCodeRepository.add(resetCode);
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