package com.experiment.first;


import org.junit.Test;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.awt.*;
import java.awt.font.OpenType;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class VIdeoDeal {



    @Test
    public void test(){
       // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        URL url = ClassLoader.getSystemResource("lib/libopencv_java450.so");
        System.load(url.getPath());
        VideoCapture videoCapture = new VideoCapture("/home/zemingyan/PycharmProjects/cs4365-task-offload" +
                "-framework/ball_tracking_example/ball_tracking_example.mp4", 1);

      //  VideoCapture videoCapture = new VideoCapture("http://192.168.31.92:8080/demo.mp4", 1);
        //video2frame();
        //image2video();
        video2file();
    }


    public static void video2file() {
//读取视频文件
        VideoCapture cap = new VideoCapture("/home/zemingyan/PycharmProjects/cs4365-task-offload" +
                "-framework/ball_tracking_example/ball_tracking_example.mp4");
       // VideoCapture cap = new VideoCapture("http://192.168.31.92:8080/demo.mp4", 1);
        System.out.println(cap.isOpened());
//判断视频是否打开
        if (cap.isOpened()) {
//总帧数



            double frameCount = cap.get(7);
            System.out.println("视频总帧数:"+frameCount);
//帧率
            double fps = cap.get(5);
            System.out.println("视频帧率"+fps);
//时间长度
            double len = frameCount / fps;
            System.out.println("视频总时长:"+len);
            Double d_s = new Double(len);
            System.out.println(d_s.intValue());
            Mat frame = new Mat();
            for (int i = 0; i < d_s.intValue(); i++) {
//设置视频的位置(单位:毫秒)
                cap.set(0, i * 1000);
//读取下一帧画面
                if (cap.read(frame)) {
                    System.out.println("正在保存");
//保存画面到本地目录
                    //HighGui.imwrite("/home/fang/images/" + i + ".jpg", frame);

                    File outputFile = new File("/home/zemingyan/images/" + i + ".jpg");
                    BufferedImage image = (BufferedImage) HighGui.toBufferedImage(frame);
                    try {
                        ImageIO.write(image, "jpg", outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//关闭视频文件
            cap.release();
        }
    }

    public static void video2frame() {
//读取视频文件
        VideoCapture cap = new VideoCapture("/home/zemingyan/PycharmProjects/cs4365-task-offload" +
                "-framework/ball_tracking_example/ball_tracking_example.mp4");
        System.out.println(cap.isOpened());
//判断视频是否打开
        if (cap.isOpened()) {
//总帧数
            double frameCount = cap.get(7);
            System.out.println("视频总帧数:"+frameCount);
            Mat frame = new Mat();
            Integer frameCnt = (int)frameCount;
            for (int i = 0; i < frameCnt; i ++){
                cap.set(1, i);
                if (cap.read(frame)){
                    System.out.println("正在保存");
//保存画面到本地目录
                    //HighGui.imwrite("/home/fang/images/" + i + ".jpg", frame);

                    File outputFile = new File("/home/zemingyan/images/" + i + ".jpg");
                    BufferedImage image = (BufferedImage) HighGui.toBufferedImage(frame);
                    try {
                        ImageIO.write(image, "jpg", outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

//关闭视频文件
            cap.release();
        }
    }


    public void image2video(){
        int fourcc = VideoWriter.fourcc('M','P','4','V');
        System.out.println("准备视频格式");
        VideoCapture videoCapture = new VideoCapture("/home/zemingyan/PycharmProjects/cs4365-task-offload" +
                "-framework/ball_tracking_example/ball_tracking_example.mp4");
        Size frameSize = new Size((int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),(int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        System.out.println("帧率设置");
        VideoWriter videoWriter = new VideoWriter("/home/zemingyan/testfile.mp4", fourcc, 20, frameSize, true);
        System.out.println("初始化输出视频");
        Mat mat = null;
        for (int i = 0; i < 1137; i ++){
            File outputFile = new File("/home/zemingyan/images/" + i + ".jpg");
            Mat tempMat = new Mat();
            try {
                BufferedImage bufferedImage = ImageIO.read(outputFile);
                mat = bufferToMartix(bufferedImage);
                videoWriter.set(1,i);


                videoWriter.write(mat);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("视频构造完毕");
        videoWriter.release();

    }


    public  Mat bufferToMartix(BufferedImage image) {
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
