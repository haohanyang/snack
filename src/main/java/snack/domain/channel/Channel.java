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
    private Timestamp createdAt;

    public Channel() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
