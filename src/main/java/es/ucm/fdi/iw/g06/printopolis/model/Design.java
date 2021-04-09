package es.ucm.fdi.iw.g06.printopolis.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@NamedQueries({
		@NamedQuery(name = "Design.allUserDesigns", query = "SELECT d FROM Design d "
				+ "WHERE d.designer.id = :userId"),

		@NamedQuery(name = "Design.categoryDesigns", query = "SELECT d FROM Design d "
				+ "WHERE d.category = :category"),
		@NamedQuery(name = "Design.listAll", query = "SELECT d FROM Design d"),

		@NamedQuery(name = "Design.getDesign", query = "SELECT d FROM Design d "
		+ "WHERE d.id = :designId")
	})
@Data
public class Design {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	private String category;
	private BigDecimal price;
	private String name;
	private String description;
	private int puntuation;
	private float dimension;
	// private int numVentas;

	@ManyToOne
	private User designer;

	@Override
	public String toString() {
		return "Design #" + id;
	}
}