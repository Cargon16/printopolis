package es.ucm.fdi.iw.g06.printopolis.model;

import java.math.BigDecimal;
import java.sql.Date;
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
		@NamedQuery(name = "Evento.getPrinterEvents", query = "SELECT e FROM Evento e WHERE e.impresora = :id"),
		@NamedQuery(name = "Evento.delEvento", query = "DELETE FROM Evento e WHERE e.id = :id")

	})
@Data
public class Evento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	private Date fechaPedido;
	private long impresora;
	private long sale;
    private String user;

	@Override
	public String toString() {
		return "Evento #" + id;
	}
}