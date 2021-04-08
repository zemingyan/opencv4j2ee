package com.experiment.first;

import com.experiment.first.rmimpl.TaskOffloadImpl;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;

public class UnitTest {
    private  Point center = new Point(0,0);
    private Mat MatDeal(Mat mat) {
        System.out.println("帧的数据,通道数 " + mat.channels() + "   大小" + mat.rows() + "   " + mat.cols()
            + "   类型" + mat.type() + " 深度 " + mat.depth());
        float[] radius = new float[100];
        // 5 根据自己的需求如果不需要再使用原来视频中的图像帧可以只需要上面一个 Mat video 我这里未来展示 处理后图像跟踪的效果
        Mat dealvideo = new Mat();
        Mat temp = new Mat();

        Imgproc.GaussianBlur(mat, temp, new Size(11,11), 0);
        Mat hsvMat = new Mat();
        Imgproc.cvtColor(temp, hsvMat, Imgproc.COLOR_BGR2HSV_FULL);
        imshow("高斯，hsv结果", hsvMat);


       // Core.inRange(hsvMat,new Scalar(0, 127, 0),new Scalar(120, 255, 120), dealvideo);
        Core.inRange(hsvMat,new Scalar(29, 86, 6),new Scalar(64, 255, 255), dealvideo);

        imshow("颜色过滤", dealvideo);
        Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3, 3),new Point(-1, -1));

        Mat reduce = new Mat();
        Imgproc.erode(dealvideo, reduce, kernel1);
        imshow("fushi效果", reduce);



     /*   Imgproc.dilate(dealvideo, dealvideo, kernel2,new Point(-1, -1), 4);
        imshow("扩展", dealvideo);
        // 11 找出对应物体在图像中的坐标位置(X,Y)及宽、高(width,height)轮廓发现与位置标定
        Rect rects = new Rect();
        rects = process(dealvideo, rects, radius);
        //Imgproc.rectangle(mat,rects,new Scalar(0, 0, 255), 3, 8, 0);
        if (rects.width != 0 && rects.height != 0){
            System.out.println("划线 " + center.toString() + "  " + radius[0]);
            Imgproc.circle(mat, center, 5, new Scalar(0,0,255), -1);
            Imgproc.circle(mat, center, (int)radius[0], new Scalar(0,0,255), 2);
        }
        imshow("111", mat);
        waitKey();*/
        return  mat;
    }

    public  Rect process(Mat dealvideo, Rect rects, float[] radius) {
        // 1 跟踪物体在图像中的位置
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        // 2
        Mat hierarchy=new Mat();
        // 3 找出图像中物体的位置
        Imgproc.findContours(dealvideo, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0, 0));
        //Imgproc.findContours(dealvideo, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE,new Point(0, 0));
        if (contours.size() > 0) {// 4.1 如果发现图像
            double maxarea = 0.0;
            for (int t = 0; t < contours.size(); t++) {
                double area = Imgproc.contourArea(contours.get(t));
                if (area > maxarea) {
                    Moments moments = Imgproc.moments(dealvideo);
                    maxarea = area;
                    rects = Imgproc.boundingRect(contours.get(t));

                    MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
                    contours.get(t).convertTo(matOfPoint2f, CvType.CV_32FC2);

                    center = new Point((int)moments.m10/moments.m00,(int) moments.m01/moments.m00);
                    System.out.println("中心店位置 " + center.toString());
                    Imgproc.minEnclosingCircle(  matOfPoint2f, center, radius);
                }
            }
        } else {// 4.2 如果没有发现图像
            rects.x = rects.y = rects.width = rects.height = 0;
        }
        return rects;
    }



    @Test
    public void test(){
        URL url = ClassLoader.getSystemResource("lib/libopencv_java450.so");
        System.load(url.getPath());
        Mat temp = new Mat();

        // capture.read(video);
        File outputFile = new File("/home/zemingyan/images/15.jpg");
            /*byte[] bytes = FileUtils.readFileToByteArray(new File("aaa.jpg"));
            Mat mat = Imgcodecs.imdecode(new MatOfByte(bytes), 1);
            videoWriter.set(1, i);
            videoWriter.write(mat);*/
        try {
            BufferedImage bufferedImage = ImageIO.read(outputFile);
            temp = ObjectTracking.bufferToMartix(bufferedImage);
            System.out.println("帧信息  " + temp.cols() + "   " + temp.height());
            Mat res = new UnitTest().MatDeal(temp);
            imshow("11", res);
            waitKey();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
