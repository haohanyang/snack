package snack.domain.message;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import snack.domain.user.User;

@Entity(name = "fcm_registration_token")
@Table(name = "fcm_registration_tokens")
@Data
public class FCMRegistrationToken {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "fcm_registration_token_gen", sequenceName = "fcm_registration_token_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fcm_registration_token_gen")
    private Integer id;

    @Column(name = "token", length = 300, nullable = false, unique = true)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "sns_endpoint_arn", length = 200, nullable = false)
    private String snsEndpointArn;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}
