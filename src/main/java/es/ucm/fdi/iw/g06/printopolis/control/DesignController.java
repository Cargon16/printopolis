package es.ucm.fdi.iw.g06.printopolis.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import es.ucm.fdi.iw.g06.printopolis.LocalData;
import es.ucm.fdi.iw.g06.printopolis.model.Design;
import es.ucm.fdi.iw.g06.printopolis.model.Sales;
import es.ucm.fdi.iw.g06.printopolis.model.User;
import es.ucm.fdi.iw.g06.printopolis.model.SalesLine;

@Controller()
@RequestMapping("design")
public class DesignController {
	private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;

	@Transactional
	@RequestMapping(value = "/addDesign", method = RequestMethod.POST)
	public String addDesign(@RequestParam("diseno") String diseno, @RequestParam("category") String categoria,
			@RequestParam("precio") float precio, @RequestParam("about") String about,
			@RequestParam("volumen") float volumen, @RequestParam("fichero") MultipartFile archivo,
			@RequestParam("captura") MultipartFile captura, Model model, HttpSession session) throws IOException {

		Design d = new Design();
		d.setCategory(categoria);
		d.setDescription(about);
		d.setDimension(volumen);
		d.setName(diseno);
		d.setPrice(precio);
		d.setDesigner(((User) session.getAttribute("u")));
		entityManager.persist(d);
		entityManager.flush();
		File f = localData.getFile("design", ""+ d.getId());
		File c = localData.getFile("design", ""+ d.getId() + "c");
		if (archivo.isEmpty()) {
			log.info("failed to upload design: emtpy file?");
		} else {
			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = archivo.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				log.warn("Error uploading " + d.getId() + " ", e);
			}
			log.info("Successfully uploaded file {} into {}!", d.getId(), f.getAbsolutePath());
			if (!captura.isEmpty()) {
				try (BufferedOutputStream stream1 = new BufferedOutputStream(new FileOutputStream(c))) {
					byte[] bytess = captura.getBytes();
					stream1.write(bytess);
				} catch (Exception e1) {
					log.warn("Error uploading " + d.getId() + " ", e1);
				}
			}
		}
		log.info("Added new design {}", diseno);

		return "redirect:/";
	}


	@Transactional
	@RequestMapping(value = "/modDesign/{id}", method = RequestMethod.POST)
	public String modifyDesign(@PathVariable long id, @RequestParam("diseno") String diseno, @RequestParam("category") String categoria,
			@RequestParam("precio") float precio, @RequestParam("about") String about, Model model, HttpSession session) throws IOException {

		Design d = entityManager.find(Design.class, id);
		if(d.getDesigner().getId() == ((User) session.getAttribute("u")).getId()){


		d.setCategory(categoria);
		d.setDescription(about);
		d.setName(diseno);
		d.setPrice(precio);
		entityManager.persist(d);

		log.info("Added new design {}", diseno);
	}
		return "redirect:/";
	}

	@GetMapping(value = "/{id}/design")
	public StreamingResponseBody get3dModel(@PathVariable long id, Model model) throws IOException {
		File f = localData.getFile("design", "" + id);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			log.info("Failed to load the file", id);
			in = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("static/img/object.png"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}

	@GetMapping(value = "/{id}/captura")
	public StreamingResponseBody getCaptura(@PathVariable long id, Model model) throws IOException {
		File f = localData.getFile("design", "" + id + "c");
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			log.info("Failed to load the file", id);
			in = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("static/img/object.png"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}


	@Transactional
	@GetMapping("/{category}")
	public String designs(@PathVariable String category, Model model, HttpServletRequest request, HttpSession session) {
		List<Design> l;

		if(category.equals("all")){
				l = entityManager.createQuery("SELECT d FROM Design d").getResultList();
		}
		else {
				l = entityManager.createNamedQuery("Design.categoryDesigns", Design.class).setParameter("category", category).getResultList();
		}
		List<Long> lastId = entityManager.createQuery("SELECT d.id FROM Design d ORDER BY d.id DESC").getResultList();
		int len = lastId.get(0).intValue() + 1;
		Integer [] k = new Integer [len];
		User user = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());
		for (Design d : l) {
			Integer id = ((int)d.getId());
			if(d.getUsersLikes().contains(user)){
				k[id] =1;
			}
			else{
				k[id] = 0;
			}

		}

		model.addAttribute("likeList", k);
		model.addAttribute("categoryType", l);
		model.addAttribute("categoryName", category);
		log.info("Sending a message to {} with contents '{}'", l);

		return "designs";
	}

	@Transactional
	@RequestMapping(value = "/name", method = RequestMethod.GET)
	public String nameDesigns(@RequestParam("search") String name, Model model, HttpServletRequest request, HttpSession session) {
		List<Design> l;
		l = entityManager.createQuery("SELECT d FROM Design d WHERE d.name like :name").setParameter("name", "%" + name + "%").getResultList();
		List<Long> lastId = entityManager.createQuery("SELECT d.id FROM Design d ORDER BY d.id DESC").getResultList();
		int len = lastId.get(0).intValue() + 1;
		Integer [] k = new Integer [len];
		User user = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());
		for (Design d : l) {
			Integer id = ((int)d.getId());
			if(d.getUsersLikes().contains(user)){
				k[id] =1;
			}
			else{
				k[id] = 0;
			}

		}

		model.addAttribute("likeList", k);
		model.addAttribute("categoryType", l);
		model.addAttribute("categoryName", name);
		

		return "designs";
	}


	@PostMapping("/addToCart")
	@Transactional
	@ResponseBody
	public String addToCart(@RequestBody JsonNode o, Model model, HttpSession session) throws JsonProcessingException {
		Long id = o.get("cart").asLong();
		User user = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());
		Design d = entityManager.createNamedQuery("Design.getDesign", Design.class).setParameter("designId", id).getSingleResult();
		Sales compra = user.getSaleId();

		if(user.getSaleId() == null){
			compra = new Sales();
			compra.setUser(user);
			compra.setAddress(user.getAddress());
			compra.setPaid(false);
			compra.setTotal_price(0);
			entityManager.persist(compra);
			entityManager.flush();
			user.setSaleId(compra);
		}
			SalesLine sl = new SalesLine();
			((User)session.getAttribute("u")).setSaleId(compra);
			sl.setDate(LocalDateTime.now());
			sl.setDesign(d.getId());
			sl.setPrice(d.getPrice());
			compra.setTotal_price(compra.getTotal_price() + d.getPrice());
			sl.setSale(compra.getId());
			entityManager.persist(sl);

		log.info("Added new product to cart {}", d.getName());

		return "{\"name\": \"" + compra.getId() + "\"}";
	}

	@Transactional
	@PostMapping("/delDesign/{id}")
	public String delDesign(@PathVariable long id, Model model, HttpSession session){
		User u = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());
		Design d = entityManager.find(Design.class, id);
		if(u.hasRole(User.Role.ADMIN) || d.getDesigner().getId() == u.getId()){
		entityManager.createNamedQuery("Design.delDesign").setParameter("id", id).executeUpdate();
		
		}

		return "redirect:/admin/";
	}

	@Transactional
	@ResponseBody
	@PostMapping("/like")
	public String like(@RequestBody JsonNode o, Model model, HttpSession session) throws IOException {
		Long id = o.get("likeId").asLong();
		User user = entityManager.find(User.class, ((User)session.getAttribute("u")).getId());
		Design d = entityManager.createNamedQuery("Design.getDesign", Design.class).setParameter("designId", id).getSingleResult();
		boolean exist = d.getUsersLikes().contains(user);
		
		if(!exist){
			d.setPunctuation(d.getPunctuation()+1);
			entityManager.persist(d);
			user.addDesignLike(d);
		}
		else{
			d.setPunctuation(d.getPunctuation()-1);
			entityManager.persist(d);
			user.delDesignLike(d);
		}
		log.info("Increment design's likes {}", d.getPunctuation());

		return "{\"name\": \"" + d.getId() + "\"}";
	}

	@RequestMapping(value = "/numLikes", method = RequestMethod.GET)
	@Transactional
	@ResponseBody
	public String getLikes(@RequestParam(name = "id", required = false) Long id, Model model, HttpSession session) throws IOException {
		try{
		Long cont = entityManager.createNamedQuery("Design.numLikes", Long.class).setParameter("id", id).getSingleResult();
		return "{\"num\": \"" + cont + "\"}";
		}catch(Exception e){
			return "{\"num\": \"" + 0 + "\"}";
		}
	 }
}
