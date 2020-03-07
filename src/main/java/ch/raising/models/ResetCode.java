package ch.raising.models;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class ResetCode {
    private final String code;
    private final int accountId;
    private final Timestamp expiresAt;
    private final int attempsLeft;

    public ResetCode(String code, int accountId, Date expiresAt) {
        this.attempsLeft = 3;
        this.code = code;
        this.accountId = accountId;
        this.expiresAt = new Timestamp(expiresAt.getTime());
    }

    public ResetCode(String code, int accountId, Date expiresAt, int attempsLeft) {
        this.attempsLeft = attempsLeft;
        this.code = code;
        this.accountId = accountId;
        this.expiresAt = new Timestamp(expiresAt.getTime());
    }
}