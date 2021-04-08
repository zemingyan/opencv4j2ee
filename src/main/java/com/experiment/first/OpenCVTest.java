package com.experiment.first;

import org.junit.Test;
import org.opencv.core.Mat;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.split;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.equalizeHist;
/**
 * @ClassName OpenCVTest
 * @Description TODO
 * @Author L
 * @Date Create by 2020/1/11
 */
public class OpenCVTest {
    /**
     * 测试OpenCV是否能运行：需要自行修改图片位置
     * @throws Exception 测试是否成功
     */
    @Test
    public void testOpenCV() throws Exception {
        URL url = ClassLoader.getSystemResource("lib/libopencv_java450.so");
        System.load(url.getPath());
        //填你的图片地址
        Mat image = imread("/home/zemingyan/Pictures/image.jpg", 1);
        if (image.empty()){
            throw new Exception("image is empty!");
        }
        imshow("Original Image", image);
        List<Mat> imageRGB = new ArrayList<>();
        split(image, imageRGB);
        for (int i = 0; i < 3; i++) {
            equalizeHist(imageRGB.get(i), imageRGB.get(i));
        }
        merge(imageRGB, image);
        imshow("Processed Image", image);
        waitKey();
    }
}