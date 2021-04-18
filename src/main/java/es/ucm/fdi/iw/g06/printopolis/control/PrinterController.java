package es.ucm.fdi.iw.g06.printopolis.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import ch.qos.logback.core.net.LoginAuthenticator;
import es.ucm.fdi.iw.g06.printopolis.LocalData;
import es.ucm.fdi.iw.g06.printopolis.LoginSuccessHandler;
import es.ucm.fdi.iw.g06.printopolis.model.Design;
import es.ucm.fdi.iw.g06.printopolis.model.Printer;
import es.ucm.fdi.iw.g06.printopolis.model.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Controller()
@RequestMapping("printer")
public class PrinterController {
    private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;

	@Transactional
	@RequestMapping(value = "/addPrinter", method = RequestMethod.POST)
	public String addUser(@RequestParam("name") String impresora, 
						@RequestParam("mat-level") Integer level,
						Model model, HttpSession session) throws IOException {

		Printer p = new Printer();
		p.setMaterial_level(level);
        p.setName(impresora);
        p.setPuntuation(0);
		p.setStatus("AVAILABLE");
		p.setImpresor(((User) session.getAttribute("u")));
		User us = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());
		us.addPrinter(p);
		entityManager.persist(p);
		entityManager.flush();
		
		
		log.info("Added new design {}",impresora);
		
		return "redirect:/";
	}

	@GetMapping(value="/{id}/printer")
	public StreamingResponseBody getPrinter(@PathVariable long id, Model model) throws IOException {
		File f = localData.getFile("printer", "" + id);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			log.info("Failed to load the file", id);
			in = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("static/img/printer.png"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}

	@GetMapping("/")
	public String printers(Model model, HttpServletRequest request) {
		List<User> l1= entityManager.createNamedQuery("User.allImpresores", User.class).getResultList();
		// List<Printer> l2= entityManager.createNamedQuery("Printer.allPrinters", Printer.class).getResultList();
		// model.addAttribute("printerObj", l2);

		model.addAttribute("printerDest", l1);
		log.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHHHHH {}", l1.toString());
		return "printers";
	}
	
	@Transactional
	@PostMapping("/delPrinter/{id}")
	public String delPrinter(@PathVariable long id, Model model){
		entityManager.createNamedQuery("Printer.dePrinter").setParameter("id", id).executeUpdate();
		entityManager.flush();

		return "redirect:/";
	}


}