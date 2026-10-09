package ar.edu.itba.paw.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "provinces")
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "province_id", nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
