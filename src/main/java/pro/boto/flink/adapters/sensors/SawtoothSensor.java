package pro.boto.flink.adapters.sensors;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import pro.boto.flink.domain.Sensor;

import java.io.Serializable;

public class SawtoothSensor implements CheckpointedFunction, Serializable {

  private final int numSteps;
  private int currentStep;

  private transient ListState<Integer> checkpointState;

  public SawtoothSensor(int numSteps){
    this.numSteps = numSteps;
    this.currentStep = 0;
  }

  public Sensor<Double> map(Long time) throws Exception {
    double phase = (double) currentStep / numSteps;
    currentStep = ++currentStep % numSteps;
    return new Sensor<>(time, phase);
  }

  @Override
  public void snapshotState(FunctionSnapshotContext context) throws Exception {
    checkpointState.clear();
    checkpointState.add(currentStep);
  }

  @Override
  public void initializeState(FunctionInitializationContext context) throws Exception {
    ListStateDescriptor<Integer> descriptor =
            new ListStateDescriptor<>("currentStep", Integer.class);

    checkpointState = context.getOperatorStateStore().getListState(descriptor);

    if (context.isRestored()) {
      for (Integer step: checkpointState.get()) {
        currentStep = step;
      }
    }
  }
}
