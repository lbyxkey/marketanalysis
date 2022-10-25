package indi.lby.marketanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="floatholder",uniqueConstraints = @UniqueConstraint(columnNames = {"symbol","anndate","enddate","holdername"}))
public class FloatHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "symbol",referencedColumnName = "symbol")
    private StockBasic symbol;

    @ManyToOne
    @JoinColumn(name = "anndate",referencedColumnName = "caldate")
    private TradeCal anndate;

    @ManyToOne
    @JoinColumn(name = "enddate",referencedColumnName = "caldate")
    private TradeCal enddate;

    private String holdername;

    private long holdamount;
}
