package ch.raising.models.responses;

import java.util.List;

import ch.raising.models.Account;
import ch.raising.models.Investor;
import ch.raising.models.Startup;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminAccountResponse {
	private List<Account> startups;
	private List<Account> investors;
	private List<Account> accounts;
}
