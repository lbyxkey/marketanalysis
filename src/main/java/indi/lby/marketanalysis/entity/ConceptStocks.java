package indi.lby.marketanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ConceptStocksUPK.class)
public class ConceptStocks {
    @Id
    @ManyToOne
    @JoinColumn(name = "conceptcode",referencedColumnName = "code")
    private Concept concept;

    @Id
    @ManyToOne
    @JoinColumn(name = "symbol",referencedColumnName = "symbol")
    private StockBasic stockBasic;
}
