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
@Table(name = "stockpool",uniqueConstraints = @UniqueConstraint(columnNames = {"userid","symbol"}))
public class StockPool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "userid",referencedColumnName = "id")
    MyUser user;
    @ManyToOne
    @JoinColumn(name = "adddate",referencedColumnName = "id")
    TradeCal adddate;
    @ManyToOne
    @JoinColumn(name = "symbol",referencedColumnName = "id")
    StockBasic symbol;

    String description;

}
