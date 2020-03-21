package ch.raising.models;

import java.sql.Timestamp;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetCode {
    private final String code;
    private final long accountId;
    private final Timestamp expiresAt;
}