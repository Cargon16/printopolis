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
	   List<SalesLine> l = entityManager.createNamedQuery("SalesLine.salesProducts", SalesLine.class).setParameter("id", u.getSaleId().getId()).getResultList();
       model.addAttribute("products", l);
       return "cart";
	}

	/*@GetMapping("/{category}")
	public String designs(@PathVariable String category, Model model, HttpServletRequest request) {
		List<Design> l;

		if(category.equals("all")){
				l = entityManager.createQuery("SELECT d FROM Design d").getResultList();
		}
		else {
				l = entityManager.createNamedQuery("Design.categoryDesigns", Design.class).setParameter("category", category).getResultList();
		}
		model.addAttribute("categoryType", l);
		model.addAttribute("categoryName", category);
		log.info("Sending a message to {} with contents '{}'", l);

		return "designs";
	}*/
}
