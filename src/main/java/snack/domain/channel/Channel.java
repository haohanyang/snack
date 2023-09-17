package snack.domain.channel;

import jakarta.persistence.*;

import java.sql.Timestamp;

@MappedSuperclass
public class Channel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channel_gen")
    private Integer id;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}
