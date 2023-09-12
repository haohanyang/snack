package snack.domain.message;

import java.sql.Timestamp;

import snack.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_gen")
    private Integer id;

    @Column(name = "content", length = 500)
    private String content;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    public Message() {

    }

    public Message(User author, String content) {
        this.author = author;
        this.content = content;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Integer getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Message message = (Message) o;

        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
