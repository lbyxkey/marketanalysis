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
@Table(name="lianbanholder",uniqueConstraints = @UniqueConstraint(columnNames = {"statisticsid","floatholderid"}))
public class LianBanHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "statisticsid",referencedColumnName = "id")
    LianBanStatistics statisticsid;

    @ManyToOne
    @JoinColumn(name = "floatholderid",referencedColumnName = "id")
    FloatHolder floatholderid;
}
