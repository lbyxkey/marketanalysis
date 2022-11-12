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
@Table(name="prema")
public class PreMa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "stockid",referencedColumnName = "id")
    StockBasic stockBasic;

    double prema5;

    double prema10;

    double prema20;

    double prema30;

    double prema60;

    double prema120;

    double prema250;
}
