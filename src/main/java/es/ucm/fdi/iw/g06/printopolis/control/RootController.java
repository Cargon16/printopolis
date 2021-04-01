package es.ucm.fdi.iw.g06.printopolis.control;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.catalina.security.SecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ch.qos.logback.core.net.LoginAuthenticator;
import es.ucm.fdi.iw.g06.printopolis.LocalData;
import es.ucm.fdi.iw.g06.printopolis.LoginSuccessHandler;
import es.ucm.fdi.iw.g06.printopolis.model.User;
import net.bytebuddy.asm.Advice.Local;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import es.ucm.fdi.iw.g06.printopolis.model.Design;

/**
 * Landing-page controller
 */
@Controller
public class RootController {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger log = LogManager.getLogger(RootController.class);

	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}

	@GetMapping("/designs")
	@Transactional
	public String designs(Model model, HttpServletRequest request) {

		// List<User> l = entityManager.createQuery("SELECT us.name FROM USER us JOIN DESIGN d WHERE d.designer_id = us.id").getResultList();
		// model.addAttribute("infoUser", l);
		List<Design> l1 = entityManager.createQuery("SELECT d FROM Design d").getResultList();
		model.addAttribute("allDesigns", l1);
		log.info("Sending a message to {} with contents '{}'", l1);

		return "designs";
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

	
	@GetMapping("/stlviewer")
	public String stlviewer(Model model, HttpServletRequest request) {
		File f = localData.getFile("design", Integer.toString(1));
		model.addAttribute("file", f);
		return "stlViewer";
	}

	@Transactional
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String addUser(@RequestParam("name") String name, @RequestParam("email") String mail,
			@RequestParam("password") String password, Model model, HttpSession session) throws IOException {

		User usuario = new User();
		usuario.setUsername(mail);
		usuario.setFirstName(name);
		usuario.setPassword(passwordEncoder.encode(password));
		usuario.setEnabled((byte) 1);
		usuario.setRoles("ADMIN");
		entityManager.persist(usuario);
		log.info("Sign up user {}", mail);

		return "redirect:/";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		new SecurityContextLogoutHandler().logout(request, response, auth);
		return "redirect:/";
	}
}
