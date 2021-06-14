package es.ucm.fdi.iw.g06.printopolis.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import es.ucm.fdi.iw.g06.printopolis.LocalData;
import es.ucm.fdi.iw.g06.printopolis.model.Printer;
import es.ucm.fdi.iw.g06.printopolis.model.User;
import es.ucm.fdi.iw.g06.printopolis.model.User.Role;

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
						@RequestParam("mat-level") Integer level, @RequestParam("price") Integer precio,
						Model model, HttpSession session) throws IOException {

		Printer p = new Printer();
		p.setMaterial_level(level);
        p.setName(impresora);
        p.setPunctuation(0);
		p.setStatus("AVAILABLE");
		p.setPrecio(precio);
		p.setImpresor(((User) session.getAttribute("u")));
		User us = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());
		us.addPrinter(p);
		entityManager.persist(p);
		
		
		log.info("Added new design {}",impresora);
		
		return "redirect:/";
	}

	@Transactional
	@RequestMapping(value = "/modPrinter/{id}", method = RequestMethod.POST)
	public String modifyPrinter(@PathVariable long id, @RequestParam("name") String impresora, 
						@RequestParam("mat-level") Integer level,
						@RequestParam("precioP") float precio,
						Model model, HttpSession session) throws IOException {

		Printer p = entityManager.find(Printer.class, id);
		p.setMaterial_level(level);
        p.setName(impresora);
        p.setPunctuation(0);
		p.setStatus("AVAILABLE");
		p.setPrecio(precio);
		p.setImpresor(((User) session.getAttribute("u")));

		entityManager.persist(p);
		
		
		log.info("Printer modified {}",impresora);
		
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

	@Transactional
	@GetMapping("/")
	public String printers(Model model, HttpServletRequest request) {
		List<User> l1= entityManager.createNamedQuery("User.allImpresores", User.class).getResultList();

		model.addAttribute("printerDest", l1);
		return "printers";
	}
	
	@Transactional
	@PostMapping("/delPrinter/{id}")
	public String delPrinter(@PathVariable long id, Model model, HttpSession session){
		User u = (User)session.getAttribute("u");
		Printer p = entityManager.find(Printer.class, id);

		if(p.getImpresor().getId() == u.getId() || u.hasRole(Role.ADMIN)){
		entityManager.createNamedQuery("Printer.dePrinter").setParameter("id", id).executeUpdate();
		}

		return "redirect:/";
	}

	@GetMapping("/ordenarAsc")
	public String ordenAsc(Model model, HttpServletRequest request) {
		List<User> l1= entityManager.createNamedQuery("User.ordenAsc", User.class).getResultList();

		model.addAttribute("printerDest", l1);
		return "printers";
	}

	@GetMapping("/ordenarDesc")
	public String ordenDesc(Model model, HttpServletRequest request) {
		List<User> l1= entityManager.createNamedQuery("User.ordenDesc", User.class).getResultList();

		model.addAttribute("printerDest", l1);
		return "printers";
	}
	
	@PostMapping("/modifyPrice/{id}")
	@Transactional
	@ResponseBody
	public String changePrice(@PathVariable long id, @RequestBody JsonNode o, Model model, HttpSession session){
		int precio = o.get("price").asInt(-1);;
		User u = (User)session.getAttribute("u");
		Printer p = entityManager.find(Printer.class, id);

		if(p.getImpresor().getId() == u.getId()){
			p.setPrecio(precio);
			entityManager.persist(p);
		}

		return "{\"name\": \"" + p.getPrecio() + "\"}";
	}

}