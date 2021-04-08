package com.experiment.first.rmi;

import com.experiment.first.pojo.TaskDTO;
import org.opencv.core.Mat;

import java.util.List;

public interface TaskOffload {
    public TaskDTO offloadTask(TaskDTO taskDTO);
}
