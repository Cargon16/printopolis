package es.ucm.fdi.iw.g06.printopolis.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.catalina.security.SecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import antlr.debug.Event;
import ch.qos.logback.core.net.LoginAuthenticator;
import es.ucm.fdi.iw.g06.printopolis.LocalData;
import es.ucm.fdi.iw.g06.printopolis.LoginSuccessHandler;
import es.ucm.fdi.iw.g06.printopolis.model.Design;
import es.ucm.fdi.iw.g06.printopolis.model.Evento;
import es.ucm.fdi.iw.g06.printopolis.model.Printer;
import es.ucm.fdi.iw.g06.printopolis.model.User;
import es.ucm.fdi.iw.g06.printopolis.model.SalesLine;
import es.ucm.fdi.iw.g06.printopolis.model.Sales;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Controller()
@RequestMapping("sale")
public class SalesController {
	private static final Logger log = LogManager.getLogger(SalesController.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@GetMapping("/{id}")
	public String getSale(@PathVariable long id, Model model) throws IOException {
	   List<SalesLine> l = entityManager.createNamedQuery("SalesLine.salesProducts", SalesLine.class).setParameter("id", id).getResultList();
       model.addAttribute("products", l);
	   
       return "cart";
	}

    @GetMapping("/{id}/payments")
	public String getPayment(@PathVariable long id, Model model) throws IOException {
	   Sales l = entityManager.createNamedQuery("Sales.sale", Sales.class).setParameter("id", id).getSingleResult();
       model.addAttribute("sales", l);
       return "payment";
	}

	@GetMapping("/")
	public String openCart(Model model, HttpSession session) throws IOException {
	   User u = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());
	   List<SalesLine> l;
	   if(u.getSaleId() != null)
	   l = entityManager.createNamedQuery("SalesLine.salesProducts", SalesLine.class).setParameter("id", u.getSaleId().getId()).getResultList();
	   else
	   l = new ArrayList<SalesLine>();

	   List<Printer> p = entityManager.createNamedQuery("Printer.allPrinters", Printer.class).getResultList();

       model.addAttribute("products", l);
	   model.addAttribute("printers", p);
       return "cart";
	}

	@Transactional
	@PostMapping("/{id}/processPayment")
	public String pay(@PathVariable long id, Model model, HttpSession session) throws IOException {
		Sales compra = entityManager.find(Sales.class, id);
		compra.setPaid(true);
		entityManager.merge(compra);
		User us = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());
		us.setSaleId(null);

		List<SalesLine> l = entityManager.createNamedQuery("Sales.getProducts", SalesLine.class).setParameter("id", id).getResultList();
		for(int i = 0; i < l.size(); ++i){
			Design d = entityManager.createNamedQuery("Design.getDesign", Design.class).setParameter("designId", l.get(i).getDesign()).getSingleResult();
			d.setNumVentas(d.getNumVentas()+1);
		}

		return "redirect:/user/" + us.getId();
	 }

	@Transactional
	@ResponseBody
	@GetMapping("/numberDesign")
	public String pay(Model model, HttpSession session) throws IOException {
		try{
		User u = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());
		Long cont = entityManager.createNamedQuery("SalesLine.numProducts", Long.class).setParameter("id", u.getId()).getSingleResult();
		return "{\"num\": \"" + cont + "\"}";
		}catch(Exception e){
			return "{\"num\": \"" + 0 + "\"}";
		}
	 }

	 @Transactional
	 @GetMapping("/printerChoice/{id}")
	public String printerChoice(@PathVariable long id, Model model, HttpSession session) throws IOException {
		model.addAttribute("id", id);
		model.addAttribute("user", ((User)session.getAttribute("u")).getUsername());
		return "printerTurn";
	 }
	 @PostMapping("/addEvent")
	 @Transactional
	 @ResponseBody
	 public String addEvent(@RequestBody JsonNode o, Model model, HttpSession session) throws JsonProcessingException {
		Sales s = ((User)session.getAttribute("u")).getSaleId();
		ObjectMapper mapper = new ObjectMapper();
		Date date = mapper.convertValue(o.get("evento").get("date"), Date.class);
		Long printer = mapper.convertValue(o.get("evento").get("printer"), Long.class);
		String user = mapper.convertValue(o.get("evento").get("user"), String.class);
		String id = mapper.convertValue(o.get("evento").get("eventId"), String.class);
		Evento e = new Evento();
		s.setPrinter(printer);
		entityManager.merge(s);
		e.setFechaPedido(date);
		e.setImpresora(printer);
		e.setId(id);
		//e.setSale(sale);
		e.setUser(user);
		entityManager.persist(e);

		// construye json
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", date.toString());
		rootNode.put("user", e.getUser());
		rootNode.put("idPrinter", e.getImpresora());
		rootNode.put("id", e.getId());
		rootNode.put("created", true);
		String json = mapper.writeValueAsString(rootNode);

		messagingTemplate.convertAndSend("/topic/printer", json);
		return "{\"name\": \"" + e.getId() + "\"}";
	 }

	@RequestMapping(value = "/getEventos", method = RequestMethod.GET)
	@Transactional
	@ResponseBody
	public List<Evento> getEventos(@RequestParam(name = "id", required = false) Long id, Model model, HttpSession session) throws IOException {
		try{
			List<Evento> kes = entityManager.createNamedQuery("Evento.getPrinterEvents", Evento.class).setParameter("id", id).getResultList();
		return kes;
		}catch(Exception e){
			List<Evento> l = new ArrayList<Evento>();
			return l;
		}
	 }

	@RequestMapping(value = "/delEvento", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public String delEventos(@RequestParam(name = "id", required = false) String id, Model model, HttpSession session) throws IOException {
		entityManager.createNamedQuery("Evento.delEvento").setParameter("id", id).executeUpdate();
		entityManager.flush();

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("id", id);
		rootNode.put("created", false);
		String json = mapper.writeValueAsString(rootNode);

		messagingTemplate.convertAndSend("/topic/printer", json);

		return "{\"name\": \"" + "borrado" + "\"}";
	 }

}
