package es.ucm.fdi.iw.g06.printopolis.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.Data;

@Entity
@NamedQueries({
        @NamedQuery(name = "SalesLine.salesProducts", query = "SELECT l.id, l.design, l.price, l.sale, l.date, d.name, d.id FROM SalesLine l " + "JOIN Design d ON l.design = d.id WHERE l.sale = :id"),
        @NamedQuery(name = "SalesLine.numProducts", query = "SELECT COUNT(*) from SalesLine l JOIN Sales s ON l.sale = s.id JOIN User u ON s.id = u.saleId.id WHERE u.id = :id"),
        @NamedQuery(name = "Printer.allPrinters", query= "SELECT p FROM Printer p"),
        @NamedQuery(name = "SalesLine.getNumProducts", query = "SELECT COUNT(sl) FROM SalesLine sl WHERE sl.sale = :id"),
        @NamedQuery(name = "SalesLine.delProd", query = "DELETE FROM SalesLine sl WHERE sl.design = :id"),
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
