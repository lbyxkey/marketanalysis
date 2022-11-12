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
@Table(name="ma")
public class Ma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stockid",referencedColumnName = "id")
    StockBasic stockBasic;

    @ManyToOne
    @JoinColumn(name = "tradedateid",referencedColumnName = "id")
    private TradeCal tradedate;

    double ma5;

    double ma10;

    double ma20;

    double ma30;

    double ma60;

    double ma120;

    double ma250;
}
