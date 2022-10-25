package indi.lby.marketanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="lianbanstatistics",uniqueConstraints = @UniqueConstraint(columnNames = {"symbol","startdate"}))
public class LianBanStatistics {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @ManyToOne
    @JoinColumn(name = "symbol",referencedColumnName = "symbol")
    StockBasic symbol;

    @ManyToOne
    @JoinColumn(name = "startdate",referencedColumnName = "caldate")
    TradeCal startdate;
    @ManyToOne
    @JoinColumn(name = "enddate",referencedColumnName = "caldate")
    TradeCal enddate;
    int count;
}
