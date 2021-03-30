package es.ucm.fdi.iw.g06.printopolis.control;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.core.net.LoginAuthenticator;
import es.ucm.fdi.iw.g06.printopolis.LocalData;
import es.ucm.fdi.iw.g06.printopolis.LoginSuccessHandler;
import es.ucm.fdi.iw.g06.printopolis.model.Design;

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
            @RequestParam("volumen") float volumen, @RequestParam("fichero") MultipartFile archivo, Model model, HttpSession session) throws IOException {

        Design d = new Design();
        d.setCategory(categoria);
        d.setDescription(about);
        d.setDimension(volumen);
        d.setName(diseno);
        d.setPrice(precio);
        entityManager.persist(d);
        entityManager.flush();
        File f = localData.getFile("design", Long.toString(d.getId()));
		if (archivo.isEmpty()) {
			log.info("failed to upload design: emtpy file?");
		} else {
			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = archivo.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				log.warn("Error uploading " + d.getId() + " ", e);
			}
			log.info("Successfully uploaded file {} into {}!", d.getId(), f.getAbsolutePath());
		}
        log.info("Added new design {}", diseno);

        return "redirect:/";
    }

}
