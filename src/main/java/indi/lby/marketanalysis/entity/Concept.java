package indi.lby.marketanalysis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="concept")
public class Concept{

    @ManyToOne
    @JoinColumn(name = "type",referencedColumnName = "type")
    private ConceptType conceptType;
    //private Integer type;
    @Id
    private String code;

    private String name;
}
