package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.raising.data.StartupRepository;
import ch.raising.models.Startup;

@Service
public class StartupService {

	StartupRepository startuprepo;
	
	@Autowired
	public StartupService(StartupRepository startuprepo){}
	
	public Startup getStartupById(int id){
		Startup startup = startuprepo.getStartupById(id);
		//add label to startup
		
		
		return startup;
	}
	
	

}
