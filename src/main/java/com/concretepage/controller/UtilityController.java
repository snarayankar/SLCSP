package com.concretepage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concretepage.service.UtilityService;

@Controller
@RequestMapping("util")
public class UtilityController {

	@Autowired
	private UtilityService utilityService;
	
	@GetMapping("import")
	public ResponseEntity<String> getArticleById() {
		utilityService.processSLCSP();
		return new ResponseEntity<String>("Process Completed", HttpStatus.OK);
	}
	
}
