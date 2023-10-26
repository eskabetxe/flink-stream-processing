package pro.boto.flink.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ControlMessage {

  public final String sensor;
  public final Double amplitude;

  public ControlMessage(){
      this(null, null);
  }

  public ControlMessage(String sensor, Double amplitude){
    this.sensor = sensor;
    this.amplitude = amplitude;
  }

  public static ControlMessage fromString(String s){
    String[] parts = s.split(" ");
    if(parts.length == 2) {
      String sensor = parts[0];
      double amplitude = Double.parseDouble(parts[1]);
      return new ControlMessage(sensor, amplitude);
    } else {
      return new ControlMessage();
    }
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
            .append("sensor", sensor)
            .append("amplitude", amplitude)
            .toString();
  }
}
