package es.ucm.fdi.iw.g06.printopolis.control;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Landing-page controller
 */
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
	@Transactional
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

	@GetMapping("/payment")
	public String payment(Model model, HttpServletRequest request) {
		return "payment";
	}

	@GetMapping("/cart")
	public String cart(Model model, HttpServletRequest request) {
		return "cart";
	}

	@GetMapping("/error")
	public String error(Model model, HttpServletRequest request) {
		return "error";
	}
	@GetMapping("/login")
	public String login() {
		return "login";
	}
}


