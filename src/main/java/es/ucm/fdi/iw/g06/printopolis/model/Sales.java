package es.ucm.fdi.iw.g06.printopolis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@NamedQueries({ @NamedQuery(name = "Sales.sale", query = "SELECT l FROM Sales l " + "WHERE l.id = :id"),
		@NamedQuery(name = "Sales.getProducts", query = "SELECT l from SalesLine l JOIN Sales s ON l.sale = s.id "
				+ "WHERE s.id = :id"),
		@NamedQuery(name = "Sales.getUserSales", query = "SELECT l from Sales l WHERE l.user.id = :id"),
		@NamedQuery(name = "Sales.getAllSales", query = "SELECT distinct l.design from SalesLine l JOIN Sales s ON l.sale = s.id WHERE s.user.id = :id"),
		@NamedQuery(name = "Sales.getHistory", query = "SELECT sl from SalesLine sl JOIN Sales s ON sl.sale = s.id WHERE s.user.id = :id ORDER BY sl.sale")

})
@Data
public class Sales {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@OneToOne
	private User user;
	private String address;
	private long printer;
	private float total_price;
	private Boolean paid;

	@Override
	public String toString() {
		return "Sale #" + id;
	}

}
