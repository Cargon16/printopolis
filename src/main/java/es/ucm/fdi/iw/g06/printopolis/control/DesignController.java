package es.ucm.fdi.iw.g06.printopolis.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import ch.qos.logback.core.net.LoginAuthenticator;
import es.ucm.fdi.iw.g06.printopolis.LocalData;
import es.ucm.fdi.iw.g06.printopolis.LoginSuccessHandler;
import es.ucm.fdi.iw.g06.printopolis.model.Design;
import es.ucm.fdi.iw.g06.printopolis.model.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

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
	public String addUser(@RequestParam("diseno") String diseno, @RequestParam("category") String categoria,
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
		File f = localData.getFile("design", Long.toString(d.getId()));
		File c = localData.getFile("design", Long.toString(d.getId()) + "c");
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

}
