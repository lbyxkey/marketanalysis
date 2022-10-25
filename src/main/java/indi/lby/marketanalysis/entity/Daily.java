package indi.lby.marketanalysis.entity;

import lombok.*;

import javax.persistence.*;
import  java.sql.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DailyUPK.class)
@Table(name="daily")
public class Daily {
    @Id
    @ManyToOne
    @JoinColumn(name = "tradedate",referencedColumnName = "caldate")
    private TradeCal tradedate;
    private double open;
    private double high;
    private double low;
    private double close;
    private double preclose;
    private double change;
    private double pctchg;
    private double vol;
    private double amount;
    @Id
    @ManyToOne
    @JoinColumn(name = "symbol",referencedColumnName = "symbol")
    private StockBasic symbol;

}
