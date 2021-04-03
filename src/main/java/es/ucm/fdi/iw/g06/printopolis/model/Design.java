package es.ucm.fdi.iw.g06.printopolis.model;

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
	@NamedQuery(name="Design.allUserDesigns",
			query="SELECT d FROM Design d "
					+ "WHERE d.designer.id = :userId"),
					
	@NamedQuery(name="Design.categoryDesigns",
			query="SELECT d FROM Design d "
					+ "WHERE d.category = :category")
})
@Data
public class Design {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	private String category;
	private float price;
	private String name;
	private String description;
	private int puntuation;
	private float dimension;

	@ManyToOne
	private User designer;

	@Override
	public String toString() {
		return "Design #" + id;
	}	
}