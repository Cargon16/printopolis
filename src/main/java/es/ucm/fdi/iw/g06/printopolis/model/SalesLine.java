package es.ucm.fdi.iw.g06.printopolis.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@NamedQueries({
        @NamedQuery(name = "SalesLine.salesProducts", query = "SELECT l.id, l.design, l.price, l.sale, l.date, d.name FROM SalesLine l " + "JOIN Design d ON l.design = d.id WHERE l.sale = :id"),
        @NamedQuery(name = "SalesLine.numProducts", query = "SELECT COUNT(*) from SalesLine l JOIN Sales s ON l.sale = s.id JOIN User u ON s.id = u.SaleId.id WHERE u.id = :id"),
        @NamedQuery(name = "Printer.allPrinters", query= "SELECT p FROM Printer p"),
        @NamedQuery(name = "SalesLine.getTotalPrice", query = "SELECT SUM(sl.price) FROM SalesLine sl WHERE sl.sale = :id"),
        // @NamedQuery(name = "SalesLine.delProd", query = "DELETE FROM SalesLine sl WHERE sl.id = :id"),
    })
@Data
public class SalesLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long design;
    private long sale;
    private float price;

    private LocalDateTime date;

    @Override
    public String toString() {
        return "Sale_line #" + id;
    }

}
