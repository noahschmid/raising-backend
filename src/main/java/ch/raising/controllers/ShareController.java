package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.ShareService;

@Controller
@RequestMapping("/share")
public class ShareController {
	
	ShareService shareService;
	
	@Autowired
	public ShareController(ShareService shareService) {
		this.shareService = shareService;
	}
	
	
	
	
	
}
