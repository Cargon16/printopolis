package es.ucm.fdi.iw.g06.printopolis.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.g06.printopolis.LocalData;
import es.ucm.fdi.iw.g06.printopolis.model.Design;
import es.ucm.fdi.iw.g06.printopolis.model.Evento;
import es.ucm.fdi.iw.g06.printopolis.model.Printer;
import es.ucm.fdi.iw.g06.printopolis.model.User;
import es.ucm.fdi.iw.g06.printopolis.model.SalesLine;
import es.ucm.fdi.iw.g06.printopolis.model.Sales;

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
		List<SalesLine> l = entityManager.createNamedQuery("SalesLine.salesProducts", SalesLine.class)
				.setParameter("id", id).getResultList();

		model.addAttribute("products", l);

		return "cart";
	}

	@GetMapping("/{id}/payments")
	public String getPayment(@PathVariable long id, Model model) throws IOException {
		Sales l = entityManager.createNamedQuery("Sales.sale", Sales.class).setParameter("id", id).getSingleResult();
		model.addAttribute("sales", l);
		return "payment";
	}

	@Transactional
	@GetMapping("/")
	public String openCart(Model model, HttpSession session) throws IOException {
		User u = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());
		List<Object> l;

		if (u.getSaleId() != null) {
			Sales t = entityManager.find(Sales.class, u.getSaleId().getId());
			Printer p = entityManager.find(Printer.class, u.getSaleId().getPrinter());

			if (p != null) {

				model.addAttribute("printer", p.getName());
				model.addAttribute("printerPrice", p.getPrecio());
				t.setTotal_price(t.getTotal_price());

				model.addAttribute("price", t.getTotal_price());
			} else {
				model.addAttribute("printer", null);
				model.addAttribute("price", t.getTotal_price());
			}
			l = entityManager.createNamedQuery("SalesLine.salesProducts").setParameter("id", u.getSaleId().getId())
					.getResultList();

			List<Evento> e = entityManager.createNamedQuery("Evento.getEvento", Evento.class)
					.setParameter("id", u.getSaleId().getId()).getResultList();
			if (!e.isEmpty()) {
				model.addAttribute("evento", e.get(0).toString());
				model.addAttribute("eventId", e.get(0).getId());
			} else
				model.addAttribute("evento", "-");

			entityManager.persist(t);
		} else {
			model.addAttribute("printer", null);
			l = new ArrayList<Object>();
		}

		List<Printer> p = entityManager.createNamedQuery("Printer.allPrinters", Printer.class).getResultList();
		model.addAttribute("coins", u.getGanancias());
		model.addAttribute("products", l);
		model.addAttribute("printers", p);
		return "cart";
	}

	@Transactional
	@ResponseBody
	@PostMapping("/applyDiscount")
	public String usePrintoCoins(Model model, HttpSession session) throws IOException {
		User user = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());

		Sales s = user.getSaleId();
		s.setTotal_price(s.getTotal_price() - user.getGanancias());
		user.setGanancias(0);
		entityManager.persist(s);
		entityManager.persist(user);

		return "{\"Discount\": \"" + s.getTotal_price() + "\"}";
	}

	@RequestMapping(value = "/discount", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public String getDiscount(Model model, HttpSession session) throws IOException {
		try {
			User user = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());
			Sales s = entityManager.createNamedQuery("Sales.sale", Sales.class)
					.setParameter("id", user.getSaleId().getId()).getSingleResult();
			return "{\"discount\": \"" + s.getTotal_price() + "\"}";
		} catch (Exception e) {
			return "{\"discount\": \"" + 0 + "\"}";
		}
	}

	@Transactional
	@PostMapping("/{id}/processPayment")
	public String pay(@PathVariable long id, Model model, HttpSession session) throws IOException {
		Sales compra = entityManager.find(Sales.class, id);
		compra.setPaid(true);
		entityManager.merge(compra);
		User us = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());
		us.setSaleId(null);

		List<SalesLine> l = entityManager.createNamedQuery("Sales.getProducts", SalesLine.class).setParameter("id", id)
				.getResultList();
		for (int i = 0; i < l.size(); ++i) {
			Design d = entityManager.createNamedQuery("Design.getDesign", Design.class)
					.setParameter("designId", l.get(i).getDesign()).getSingleResult();
			d.setNumVentas(d.getNumVentas() + 1);

			float ant = d.getDesigner().getGanancias();
			d.getDesigner().setGanancias(ant + d.getPrice());

			entityManager.persist(d.getDesigner());
		}
		if (compra.getPrinter() > 0) {
			Printer p = entityManager.createNamedQuery("Printer.getPrinter", Printer.class)
					.setParameter("pId", compra.getPrinter()).getSingleResult();
			User user = entityManager.find(User.class, p.getImpresor().getId());
			user.setGanancias(user.getGanancias() + p.getPrecio());
			entityManager.persist(user);
		}

		return "redirect:/user/" + us.getId();
	}

	@Transactional
	@ResponseBody
	@GetMapping("/numberDesign")
	public String pay(Model model, HttpSession session) throws IOException {
		try {
			User u = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());
			Long cont = entityManager.createNamedQuery("SalesLine.numProducts", Long.class)
					.setParameter("id", u.getId()).getSingleResult();
			return "{\"num\": \"" + cont + "\"}";
		} catch (Exception e) {
			return "{\"num\": \"" + 0 + "\"}";
		}
	}

	@Transactional
	@GetMapping("/printerChoice/{id}")
	public String printerChoice(@PathVariable long id, Model model, HttpSession session) throws IOException {
		model.addAttribute("id", id);
		model.addAttribute("user", ((User) session.getAttribute("u")).getUsername());
		model.addAttribute("currentSale", ((User) session.getAttribute("u")).getSaleId().getId());
		model.addAttribute("currentPrinter", ((User) session.getAttribute("u")).getSaleId().getPrinter());
		return "printerTurn";
	}

	@PostMapping("/addEvent")
	@Transactional
	@ResponseBody
	public String addEvent(@RequestBody JsonNode o, Model model, HttpSession session) throws JsonProcessingException {

		Sales s = entityManager.find(Sales.class, ((User) session.getAttribute("u")).getSaleId().getId());

		ObjectMapper mapper = new ObjectMapper();
		Date date = mapper.convertValue(o.get("evento").get("date"), Date.class);
		Long printer = mapper.convertValue(o.get("evento").get("printer"), Long.class);
		String user = mapper.convertValue(o.get("evento").get("user"), String.class);
		String id = mapper.convertValue(o.get("evento").get("eventId"), String.class);
		Evento e = new Evento();
		s.setPrinter(printer);

		Printer p = entityManager.find(Printer.class, printer);
		Object numP = entityManager.createNamedQuery("SalesLine.getNumProducts")
				.setParameter("id", ((User) session.getAttribute("u")).getSaleId().getId()).getSingleResult();
		float num = Float.parseFloat(numP.toString());
		s.setTotal_price(s.getTotal_price() + p.getPrecio() * num);
		entityManager.merge(s);

		e.setFechaPedido(date);
		e.setImpresora(printer);
		e.setId(id);
		e.setSale(((User) session.getAttribute("u")).getSaleId().getId());
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
	public List<Evento> getEventos(@RequestParam(name = "id", required = false) Long id, Model model,
			HttpSession session) throws IOException {
		try {
			List<Evento> kes = entityManager.createNamedQuery("Evento.getPrinterEvents", Evento.class)
					.setParameter("id", id).getResultList();
			return kes;
		} catch (Exception e) {
			List<Evento> l = new ArrayList<Evento>();
			return l;
		}
	}

	@RequestMapping(value = "/delEvento", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public String delEventos(@RequestParam(name = "id", required = false) String id, Model model, HttpSession session)
			throws IOException {
		entityManager.createNamedQuery("Evento.delEvento").setParameter("id", id).executeUpdate();
		entityManager.flush();
		Sales s = entityManager.find(Sales.class, ((User) session.getAttribute("u")).getSaleId().getId());
		s.setPrinter(0L);
		entityManager.merge(s);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("id", id);
		rootNode.put("created", false);
		rootNode.put("user", ((User) session.getAttribute("u")).getUsername());
		String json = mapper.writeValueAsString(rootNode);

		messagingTemplate.convertAndSend("/topic/printer", json);

		return "{\"name\": \"" + "borrado" + "\"}";
	}

	@GetMapping(value = "/download/{id}")
	public void zipDownload(@PathVariable Long id, HttpServletResponse response, HttpSession session)
			throws IOException {
		File file = localData.getFile("design", ("" + id));
		String nombreBonito = entityManager.find(Design.class, id).getName();
		FileInputStream in = new FileInputStream(file);
		byte[] content = new byte[(int) file.length()];
		in.read(content);
		ServletContext sc = session.getServletContext();
		String mimetype = sc.getMimeType(file.getName());
		in.close();
		response.reset();
		response.setContentType(mimetype);
		response.setContentLength(content.length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreBonito + ".glb\"");
		org.springframework.util.FileCopyUtils.copy(content, response.getOutputStream());
	}

	@Transactional
	@ResponseBody
	@PostMapping("/delProduct")
	public String delDesign(@RequestBody JsonNode o, Model model, HttpSession session) throws JsonProcessingException {
		Long id = o.get("prodId").asLong();
		float price = (float) o.get("price").asLong();

		User u = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());
		entityManager.createNamedQuery("SalesLine.delProd").setParameter("id", id).executeUpdate();

		Sales s = entityManager.find(Sales.class, u.getSaleId().getId());
		s.setTotal_price(s.getTotal_price() - price);
		if (s.getPrinter() > 0) {
			Printer p = entityManager.find(Printer.class, s.getPrinter());
			s.setTotal_price(s.getTotal_price() - p.getPrecio());
		}
		entityManager.persist(s);
		return "{\"Product deleted\": \"" + id + "\"}";
	}
}
