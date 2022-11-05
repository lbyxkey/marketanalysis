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
@Table(name="lianbanstatistics",uniqueConstraints = @UniqueConstraint(columnNames = {"stockid","startdate"}))
public class LianBanStatistics {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @ManyToOne
    @JoinColumn(name = "stockid",referencedColumnName = "id")
    StockBasic symbol;

    @ManyToOne
    @JoinColumn(name = "startdate",referencedColumnName = "id")
    TradeCal startdate;
    @ManyToOne
    @JoinColumn(name = "enddate",referencedColumnName = "id")
    TradeCal enddate;
    int count;
}
