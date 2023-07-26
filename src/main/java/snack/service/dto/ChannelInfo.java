package snack.service.dto;

public class ChannelInfo {
    private Integer id;
    private ChannelType type;

    public ChannelInfo(Integer id, ChannelType type) {
        this.id = id;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public ChannelType getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        if (type == ChannelType.GROUP) {
            return "g" + id;
        } else {
            return "u" + id;
        }
    }
}
