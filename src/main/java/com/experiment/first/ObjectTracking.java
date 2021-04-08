package com.experiment.first;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;

public class ObjectTracking {

    public static Point center;
    private static float[] radius = new float[100];
    public static void main(String[] args) {
        URL url = ClassLoader.getSystemResource("lib/libopencv_java450.so");
        System.out.println(url.toString() + "    " + url.getPath());
        System.load(url.getPath());
       // objectTrackingBaseOnColor();
        oneMat();
    }
    public static void objectTrackingBaseOnColor() {
        // 1 创建 VideoCapture 对象
        VideoCapture capture=new VideoCapture();
        // 2 使用 VideoCapture 对象读取本地视频
        capture.open("/home/zemingyan/testfile.mp4");
        Size frameSize = new Size((int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH),(int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        System.out.println(frameSize.toString());


        // 3 获取视频处理时的键盘输入 我这里是为了在 视频处理时如果按 Esc 退出视频对象跟踪
        int index=0;
        // 4 使用 Mat video 保存视频中的图像帧 针对每一帧 做处理
        Mat video = new Mat();
        // 5 根据自己的需求如果不需要再使用原来视频中的图像帧可以只需要上面一个 Mat video 我这里未来展示 处理后图像跟踪的效果
        Mat dealvideo = new Mat();
        //Imgproc.GaussianBlur();

        // 6 获取视频的形态学结构 用于图像 开操作 降噪使用
        Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3, 3),new Point(-1, -1));
        // 7 获取视频的形态学结构 用于图像 膨胀 扩大跟踪物体图像轮廓
        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(5, 5),new Point(-1, -1));

        while (capture.read(video) ) {

            // 8 颜色过滤  (根据跟踪物体在图像中的特定颜色找出图像 Core.inRange 会将这个颜色处理为白色 其他为黑色) 我这里是找出视频中的绿色移动物体
            // 8 颜色的设置会非常影响对象跟踪的效果
            Core.inRange(video,new Scalar(29, 86, 6),new Scalar(64, 255, 255), dealvideo);
            // 9 开操作(移除其他小的噪点)
            Imgproc.morphologyEx(dealvideo, dealvideo, Imgproc.MORPH_OPEN, kernel1,new Point(-1, -1), 1);
            // 10 膨胀 (突出特定颜色物体轮廓)
            Imgproc.dilate(dealvideo, dealvideo, kernel2,new Point(-1, -1), 4);
            // 11 找出对应物体在图像中的坐标位置(X,Y)及宽、高(width,height)轮廓发现与位置标定
            Rect rects = new Rect();

            rects = process(dealvideo, rects);
            // 12.1 在物体轮廓外画矩形
            //Imgproc.rectangle(video,new Point(rects.x,rects.y), new Point(rects.x+rects.width,rects.y+rects.height),new Scalar(0, 0, 255), 3, 8, 0);// 在物体轮廓外画矩形

            if (rects.width != 0 && rects.height != 0 && radius[0] > 25){
                // 12.2 在物体轮廓外画矩形
                Imgproc.rectangle(video,rects,new Scalar(0, 0, 255), 3, 8, 0);

                Imgproc.circle(video, center, 5, new Scalar(0,0,255), -1);
                Imgproc.circle(video, center, (int)radius[0], new Scalar(0,0,255), 2);
            }


            //Imgproc.circle(video, center, 15, new Scalar(0,0,255), 2);
           // Imgproc.li
            //Imgproc.rectangle(dealvideo,rects,new Scalar(0, 0, 255), 3, 8, 0);
            //13 展示最终的效果
            imshow("基于物体颜色的对象跟踪 单对象跟踪", video);
            index= waitKey(100);
            if (index==27) {
                capture.release();
                return;
            }
        }
    }

    public static Rect process(Mat dealvideo,Rect rects) {
        // 1 跟踪物体在图像中的位置
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        // 2
        Mat hierarchy=new Mat();
        // 3 找出图像中物体的位置
        //Imgproc.findContours(dealvideo, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0, 0));
        Imgproc.findContours(dealvideo, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE,new Point(0, 0));
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
                    Imgproc.minEnclosingCircle(  matOfPoint2f, center, radius);
                }
            }
        } else {// 4.2 如果没有发现图像
            rects.x = rects.y = rects.width = rects.height = 0;
        }
        return rects;
    }



    @Before
    public static void test(){
        URL url = ClassLoader.getSystemResource("lib/libopencv_java450.so");
        System.load(url.getPath());
    }

    @Test
    public static void oneMat(){

        // 4 使用 Mat video 保存视频中的图像帧 针对每一帧 做处理
        Mat video = new Mat();
        // 5 根据自己的需求如果不需要再使用原来视频中的图像帧可以只需要上面一个 Mat video 我这里未来展示 处理后图像跟踪的效果
        Mat dealvideo = new Mat();

        Mat temp = new Mat();
        Mat temp2 = new Mat();

       // capture.read(video);
        File outputFile = new File("/home/zemingyan/images/15.jpg");
            /*byte[] bytes = FileUtils.readFileToByteArray(new File("aaa.jpg"));
            Mat mat = Imgcodecs.imdecode(new MatOfByte(bytes), 1);
            videoWriter.set(1, i);
            videoWriter.write(mat);*/
        try {
            BufferedImage bufferedImage = ImageIO.read(outputFile);
            temp = bufferToMartix(bufferedImage);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Imgproc.GaussianBlur(temp, video, new Size(11, 11), 0);

        imshow("模糊", video);
        Mat hsv = new Mat();
         Imgproc.cvtColor(video, hsv, Imgproc.COLOR_BGR2HSV);
         imshow("hsv色域", hsv);

        System.out.println("==========");

        // 6 获取视频的形态学结构 用于图像 开操作 降噪使用
        Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3, 3),new Point(-1, -1));
        // 7 获取视频的形态学结构 用于图像 膨胀 扩大跟踪物体图像轮廓
        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(5, 5),new Point(-1, -1));

        // 8 颜色过滤  (根据跟踪物体在图像中的特定颜色找出图像 Core.inRange 会将这个颜色处理为白色 其他为黑色) 我这里是找出视频中的绿色移动物体
            // 8 颜色的设置会非常影响对象跟踪的效果
            Core.inRange(video,new Scalar(29, 86, 6),new Scalar(64, 255, 255), dealvideo);
           // Imgproc.erode();
            // 9 开操作(移除其他小的噪点)
            //Imgproc.morphologyEx(dealvideo, dealvideo, Imgproc.MORPH_OPEN, kernel1,new Point(-1, -1), 1);
            // 10 膨胀 (突出特定颜色物体轮廓)
            Imgproc.dilate(dealvideo, dealvideo, kernel2,new Point(-1, -1), 4);
            // 11 找出对应物体在图像中的坐标位置(X,Y)及宽、高(width,height)轮廓发现与位置标定
            Rect rects = new Rect();

        imshow("蒙版", dealvideo);
      //  waitKey(100);
            rects = process(dealvideo, rects);
            // 12.1 在物体轮廓外画矩形
            //Imgproc.rectangle(video,new Point(rects.x,rects.y), new Point(rects.x+rects.width,rects.y+rects.height),new Scalar(0, 0, 255), 3, 8, 0);// 在物体轮廓外画矩形
            // 12.2 在物体轮廓外画矩形
            Imgproc.rectangle(video,rects,new Scalar(0, 0, 255), 3, 8, 0);
            //画圈

            if (rects.width != 0 && rects.height != 0){
                Imgproc.circle(video, center, 5, new Scalar(0,0,255), -1);
                Imgproc.circle(video, center, (int)radius[0], new Scalar(0,0,255), 2);
            }

            // Imgproc.li
            //Imgproc.rectangle(dealvideo,rects,new Scalar(0, 0, 255), 3, 8, 0);
            //13 展示最终的效果
            imshow("基于物体颜色的对象跟踪 单对象跟踪", video);
            waitKey(100);


    }

    public static   Mat bufferToMartix(BufferedImage image) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        if (mat != null) {
            try {
                mat.put(0, 0, data);
            } catch (Exception e) {
                return null;
            }
        }
        return mat;
    }
}
