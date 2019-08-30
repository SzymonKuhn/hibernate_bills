import lombok.*;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter


@Entity
public class Invoice implements EntityInterface {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;

    @Column (nullable = false)
    private LocalDateTime dateTimeCreated;
    private String clientName;

    @Column
    private boolean isPaid;
    private LocalDateTime dateTimePaid;

    @EqualsAndHashCode.Exclude
    @OneToMany (mappedBy = "invoice", fetch = FetchType.EAGER)
    private List<Product> products;

    @Formula("(SELECT SUM(p.totalGrossPrice) FROM product p WHERE p.invoice_id=id)")
    private Double sum;


    public Invoice(LocalDateTime dateTimeCreated, String clientName) {
        this.dateTimeCreated = dateTimeCreated;
        this.clientName = clientName;
        this.isPaid = false;
        this.dateTimePaid = null;
    }

    public void addProduct (Product product) {
        this.products.add(product);
    }


}
