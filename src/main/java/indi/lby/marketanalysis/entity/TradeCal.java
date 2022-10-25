package indi.lby.marketanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tradecal")
public class TradeCal {
    @Id
    @Column(name = "caldate")
    private LocalDate caldate;
    @Column(name = "isopen")
    private boolean isopen;
    @Column(name = "pretradedate")
    private LocalDate pretradedate;
}
