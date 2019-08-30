import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private double totalGrossPrice;

    @ToString.Exclude
    @ManyToOne
    private Invoice invoice;


    public Product(String name, int amount, double priceNet, double tax, Invoice invoice) {
        this.name = name;
        this.amount = amount;
        this.priceNet = priceNet;
        this.tax = tax;
        this.totalGrossPrice = amount * (priceNet + (priceNet * tax));
        this.invoice = invoice;
    }
}
