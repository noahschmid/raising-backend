package ch.raising.models;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;

@Data
public class ResetCode {
    private final String code;
    private final long accountId;
    private final Timestamp expiresAt;

    public ResetCode(String code, long accountId, Date expiresAt) {
        this.code = code;
        this.accountId = accountId;
        this.expiresAt = new Timestamp(expiresAt.getTime());
    }
}