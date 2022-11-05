package indi.lby.marketanalysis.entity;

import lombok.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stockbasic")
public class StockBasic implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String tscode;
    private String name;
    private String market;
    @ManyToOne
    @JoinColumn(name = "listdate",referencedColumnName = "id")
    private TradeCal listdate;
    private String symbol;

    @Override
    public boolean equals(Object o){
        if(this==o)return true;
        if(o==null||this.getClass()!=o.getClass())return false;
        StockBasic stockBasic=(StockBasic) o;
        return Objects.equals(stockBasic.getSymbol(), this.getSymbol());
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.symbol);
    }
//    @OneToMany(mappedBy = )
//    private List<ConceptStocks> conceptStocksList;
}
