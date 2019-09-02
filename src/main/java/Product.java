import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Product implements EntityInterface {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;

    @Column (nullable = false)
    private String name;
    private int amount;
    private double priceNet;
    private double tax;

//    @Formula(value = ("(product.amount * (product.priceNet + (product.priceNet * product.tax)))"))
    @Formula(value = ("(amount*((priceNet*tax)+(priceNet)))"))
    private double totalGrossPrice; //TODO jako formuła!!!

    @ToString.Exclude
    @ManyToOne
    private Invoice invoice;


    public Product(String name, int amount, double priceNet, double tax, Invoice invoice) {
        this.name = name;
        this.amount = amount;
        this.priceNet = priceNet;
        this.tax = tax;
        this.invoice = invoice;
    }
}
