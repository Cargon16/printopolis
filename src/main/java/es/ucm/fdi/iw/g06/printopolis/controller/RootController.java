package es.ucm.fdi.iw.g06.printopolis.controller;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RootController {
	
	private static final Logger log = LogManager.getLogger(RootController.class);

	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}
	
	@GetMapping("/profile")
	public String profile(Model model, HttpServletRequest request) {
		return "profile";
	}

	@GetMapping("/designs")
	public String designs(Model model, HttpServletRequest request) {
		return "designs";
	}
	
	@GetMapping("/admin")
	public String error(Model model) {
		return "admin";
	}	

	@GetMapping("/printers")
	public String printers(Model model, HttpServletRequest request) {
		return "printers";
	}

	@GetMapping("/signup")
	public String signup(Model model, HttpServletRequest request) {
		return "signup";
	}
}