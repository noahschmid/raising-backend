package ch.raising.services;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.InteractionRepository;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.models.AccountDetails;
import ch.raising.models.Interaction;

@Service
public class InteractionService {
	
	private final InteractionRepository interactionRepo;
	
	@Autowired
	public InteractionService(InteractionRepository interactionRepo) {
		this.interactionRepo = interactionRepo;
	}

	public List<Interaction> getAllByAccountId() throws EmptyResultDataAccessException, DataAccessException, SQLException {
		return interactionRepo.findAll(getAccountId());
	}	
	
	
	
	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}

	public void addInteraction(Interaction interaction) {
	}

}
