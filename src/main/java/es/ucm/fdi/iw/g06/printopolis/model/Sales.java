package es.ucm.fdi.iw.g06.printopolis.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@NamedQueries({
	@NamedQuery(name="Sales.sale",
			query="SELECT l FROM Sales l "
					+ "WHERE l.id = :id")

})
@Data
public class Sales {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@OneToOne
    private User user;
    private String address;
    private float total_price;
	private Boolean paid;

    @Override
	public String toString() {
		return "Sale #" + id;
	}	

}
