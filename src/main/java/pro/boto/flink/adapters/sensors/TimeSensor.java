package pro.boto.flink.adapters.sensors;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;

import java.io.Serializable;

public class TimeSensor implements CheckpointedFunction, Serializable {
  private final int periodMs;
  private final int slowdownFactor;

  private long currentTimeMs;

  // States
  private transient ListState<Long> checkpointState;

  public TimeSensor(int periodMs, int slowdownFactor){
    this.periodMs = periodMs;
    this.slowdownFactor = slowdownFactor;
    long now = System.currentTimeMillis();
    currentTimeMs = now - (now % 1000); // floor to second boundary
  }

  @Override
  public void snapshotState(FunctionSnapshotContext context) throws Exception {
    checkpointState.clear();
    checkpointState.add(currentTimeMs);
  }

  @Override
  public void initializeState(FunctionInitializationContext context) throws Exception {
    ListStateDescriptor<Long> descriptor =
            new ListStateDescriptor<>("stateTime", Long.class);

    checkpointState = context.getOperatorStateStore().getListState(descriptor);

    if (context.isRestored()) {
      for (Long time: checkpointState.get()) {
        currentTimeMs = time;
      }
    }
  }

  public Long map() throws InterruptedException {
    return currentTimeMs;
  }

  public void timeSync() throws InterruptedException {
    currentTimeMs += periodMs;
    // Sync up with real time
    long realTimeDeltaMs = currentTimeMs - System.currentTimeMillis();
    long sleepTime = periodMs + realTimeDeltaMs + randomJitter();

    if(slowdownFactor != 1){
      sleepTime = (long) periodMs * slowdownFactor;
    }

    if(sleepTime > 0) {
      Thread.sleep(sleepTime);
    }
  }

  private long randomJitter(){
    double sign = -1.0;
    if(Math.random() > 0.5){
      sign = 1.0;
    }
    return (long)(Math.random() * periodMs * sign);
  }

}
