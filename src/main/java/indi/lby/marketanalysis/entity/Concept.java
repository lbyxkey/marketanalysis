package indi.lby.marketanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="concept")
public class Concept{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name ="conceptid")
    private List<ConceptStocks> conceptStocksList;

    @ManyToOne
    @JoinColumn(name = "type",referencedColumnName = "type")
    private ConceptType conceptType;
    //private Integer type;

    private String code;

    private String name;
}
