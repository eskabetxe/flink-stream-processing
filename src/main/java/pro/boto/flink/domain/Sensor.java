package pro.boto.flink.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Sensor<T> {

    public final Long timestamp;
    public final String name;
    public final T value;

    public Sensor() {
        this(null, null, null);
    }

    public Sensor(Long timestamp, T value) {
        this(timestamp, null, value);
    }

    public Sensor(Long timestamp) {
        this(timestamp, null, null);
    }

    public Sensor(Long timestamp, String name, T value) {
        this.timestamp = timestamp;
        this.name = name;
        this.value = value;
    }

    public Sensor<T> withName(String name) {
        return new Sensor<>(this.timestamp, name, this.value);
    }

    public Sensor<T> withValue(T value) {
        return new Sensor<>(this.timestamp, this.name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("timestamp", timestamp)
                .append("name", name)
                .append("value", value)
                .toString();
    }
}
