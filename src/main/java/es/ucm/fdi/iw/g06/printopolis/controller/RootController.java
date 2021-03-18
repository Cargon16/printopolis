package es.ucm.fdi.iw.g06.printopolis.controller;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.g06.printopolis.model.Design;


@Controller
public class RootController {
	
	@Autowired 
	private EntityManager entityManager;
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

}