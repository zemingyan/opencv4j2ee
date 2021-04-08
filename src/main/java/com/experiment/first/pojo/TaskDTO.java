package com.experiment.first.pojo;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class TaskDTO {
    private Integer tid;

    private List<Mat> mats = new ArrayList<>();

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public List<Mat> getMats() {
        return mats;
    }

    public void setMats(List<Mat> mats) {
        this.mats = mats;
    }
}
