package ar.edu.itba.paw.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "alt", nullable = false)
    private String alt;

    @Column(name = "content_type", length = 50)
    private String contentType;

    @Column(name = "data", nullable = false)
    private byte[] data;
}
