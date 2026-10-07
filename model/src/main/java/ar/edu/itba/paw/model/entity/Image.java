package ar.edu.itba.paw.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Integer id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "alt", nullable = false)
    private String alt;

    @Column(name = "content_type", length = 50)
    private String contentType;

    @Column(name = "data", nullable = false)
    private byte[] data;


}