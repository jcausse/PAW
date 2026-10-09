package ar.edu.itba.paw.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "one_time_passwords")
public class OneTimePassword {
    @Id
    @Column(name = "requester_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User user;

    @Column(name = "otp_value", nullable = false)
    private String otpValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
