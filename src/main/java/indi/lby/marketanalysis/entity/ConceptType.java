package indi.lby.marketanalysis.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="concepttype")
public class ConceptType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //@Column(name = "type", nullable = false)
    private Integer type;

    //@Column(name = "name")
    private String name;

    //@Column(name = "code")
    private String code;

    @Column(name = "short")
    private String shortname;

}
