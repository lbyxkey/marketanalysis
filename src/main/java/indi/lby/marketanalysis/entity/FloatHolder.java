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
@Table(name="floatholder",uniqueConstraints = @UniqueConstraint(columnNames = {"stockid","enddate","holderid"}))
public class FloatHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stockid",referencedColumnName = "id")
    private StockBasic symbol;

    @ManyToOne
    @JoinColumn(name = "anndate",referencedColumnName = "id")
    private TradeCal anndate;

    @ManyToOne
    @JoinColumn(name = "enddate",referencedColumnName = "id")
    private TradeCal enddate;

    @ManyToOne
    @JoinColumn(name = "holderid",referencedColumnName = "id")
    private HolderList holdername;

    private long holdamount;
}
