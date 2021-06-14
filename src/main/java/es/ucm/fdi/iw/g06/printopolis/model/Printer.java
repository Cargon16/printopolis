package es.ucm.fdi.iw.g06.printopolis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@NamedQueries({
        @NamedQuery(name = "Printer.allUserPrinters", query = "SELECT p FROM Printer p "
                + "WHERE p.impresor.id = :userId"),
        @NamedQuery(name = "Printer.dePrinter", query = "DELETE FROM Printer p WHERE p.id = :id"),
        @NamedQuery(name = "Printer.getPrinter", query = "SELECT p FROM Printer p WHERE p.id = :pId"),
   })
              

@Data
public class Printer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public enum Status {
        NO_INK, WORKING, AVAILABLE
    }

    @NotNull
    private String name;
    private float material_level;
    private int punctuation;
    private String status;
    private float precio;


    @ManyToOne
    private User impresor;

    @Override
    public String toString() {
        return "Printer #" + id;
    }

}
