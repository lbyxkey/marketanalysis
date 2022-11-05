package indi.lby.marketanalysis.entity;

import lombok.*;

import javax.persistence.*;
import  java.sql.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
//@IdClass(DailyUPK.class)
@Table(name="daily")
public class Daily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tradedate",referencedColumnName = "id")
    private TradeCal tradedate;
    @Column(nullable = false)
    private double open;
    @Column(nullable = false)
    private double high;
    @Column(nullable = false)
    private double low;
    @Column(nullable = false)
    private double close;
    private double preclose;
    @Column(nullable = false)
    private double change;
    @Column(nullable = false)
    private double pctchg;
    @Column(nullable = false)
    private double vol;
    @Column(nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(name = "stockid",referencedColumnName = "id")
    private StockBasic symbol;

}
