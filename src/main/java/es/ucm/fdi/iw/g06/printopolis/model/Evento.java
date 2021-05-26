package es.ucm.fdi.iw.g06.printopolis.model;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@NamedQueries({
		@NamedQuery(name = "Evento.getPrinterEvents", query = "SELECT e FROM Evento e WHERE e.impresora = :id"),
		@NamedQuery(name = "Evento.delEvento", query = "DELETE FROM Evento e WHERE e.id = :id"),
		@NamedQuery(name = "Evento.getEvento", query = "SELECT e FROM Evento e WHERE e.sale = :id")

	})
@Data
public class Evento {

	@Id
	private String id;

	@NotNull
	private Date fechaPedido;
	private long impresora;
	private long sale;
    private String user;

	@Override
	public String toString() {
		String pattern = "dd/MM/yyyy";
		DateFormat df = new SimpleDateFormat(pattern);
		String todayAsString = df.format(fechaPedido);
		return "Tu pedido ha quedado reservado para el día:" + todayAsString;
	}
}