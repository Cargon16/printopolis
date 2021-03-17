package es.ucm.fdi.iw.g06.printopolis.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
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
	
	@ManyToMany
	private List<User> client = new ArrayList<>();

	@ManyToMany
	private List<User> printer = new ArrayList<>();


	@OneToMany
	@JoinColumn(name="design_id")
	private List<User> designer = new ArrayList<>();


	@Override
	public String toString() {
		return "Design #" + id;
	}	
}