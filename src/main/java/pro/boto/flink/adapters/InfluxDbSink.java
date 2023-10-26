package pro.boto.flink.adapters;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.Preconditions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import pro.boto.flink.domain.Sensor;

import java.util.concurrent.TimeUnit;

public class InfluxDbSink<T extends Sensor<? extends Number>> extends RichSinkFunction<T> {

  private transient InfluxDB influxDB = null;
  private static final String dataBaseName = "sensors";
  private final String measurement;

  public InfluxDbSink(String measurement){
    this.measurement = measurement;
  }

  @Override
  public void open(Configuration parameters) throws Exception {
    super.open(parameters);
    Preconditions.checkNotNull(this.measurement, "measure must be defined");
    influxDB = InfluxDBFactory.connect("http://localhost:8086", "admin", "admin");
    influxDB.setDatabase(dataBaseName);
    influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS);
  }

  @Override
  public void close() throws Exception {
    super.close();
  }

  @Override
  public void invoke(T sensor, Context context) throws Exception {
    Point point = Point.measurement(measurement)
            .time(sensor.timestamp, TimeUnit.MILLISECONDS)
            .addField("value", sensor.value)
            .tag("sensor", sensor.name)
            .build();

    influxDB.write(dataBaseName, "autogen", point);

  }
}
