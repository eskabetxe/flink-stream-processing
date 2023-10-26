package pro.boto.flink;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import pro.boto.flink.domain.ControlMessage;
import pro.boto.flink.adapters.InfluxDbSink;
import pro.boto.flink.adapters.SensorSource;
import pro.boto.flink.process.AmplifierFunction;

public class StreamProcessor {

    public static void main(String[] args) throws Exception {

        var env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        env.disableOperatorChaining();
        env.enableCheckpointing(1000);
        env.setRestartStrategy(RestartStrategies.failureRateRestart(10,
                org.apache.flink.api.common.time.Time.minutes(5),
                org.apache.flink.api.common.time.Time.seconds(10)));

        var sensors = env.addSource(new SensorSource(1, 10));

        // Write to InfluxDB.
        sensors.addSink(new InfluxDbSink<>("sensors"));

        // Compute a windowed sum over this data and write that to InfluxDB as well.
        sensors.keyBy(sensor -> sensor.name)
                .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                .sum("value")
                .addSink(new InfluxDbSink<>("summedSensors"));

        // Add a socket source
        var controlStream = env
                .socketTextStream("localhost", 9999)
                .map(ControlMessage::fromString);

        // Modulate sensor stream via control stream
        sensors.keyBy(sensor -> sensor.name)
                .connect(controlStream.keyBy(control -> control.sensor))
                .flatMap(new AmplifierFunction())
                .addSink(new InfluxDbSink<>("amplifiedSensors"));

        env.execute("stream_processing");

    }

}
