package com.git.onedayrex.videotostr.util;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.ColorSquareErrorFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.StructuralSimilarityFitStrategy;
import io.korhner.asciimg.image.converter.AsciiToImageConverter;
import io.korhner.asciimg.image.converter.AsciiToStringConverter;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageToStrUtils {

    private static final int ss = 1000;
    private static final int mi = ss * 60;
    private static final int hh = mi * 60;

    /**
     * @param path
     *            图片路径
     */
    public static void createAsciiPic(String path) throws IOException {
        // initialize cache
        AsciiImgCache cache = AsciiImgCache.create(new Font("Courier",Font.BOLD, 6));

        // load image
        BufferedImage portraitImage = ImageIO.read(new File(path));

        // initialize converters
        AsciiToImageConverter imageConverter =
                new AsciiToImageConverter(cache, new ColorSquareErrorFitStrategy());
        AsciiToStringConverter stringConverter =
                new AsciiToStringConverter(cache, new StructuralSimilarityFitStrategy());

        // image output
        ImageIO.write(imageConverter.convertImage(portraitImage), "png",
                new File("ascii_art.png"));
        // string converter, output to console
        StringBuffer stringBuffer = stringConverter.convertImage(portraitImage);
        File file = new File("temp/1.txt");
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }else {
            if (file.exists()) {
                file.delete();
            }
        }
        stringBuffer.append("\n");
        stringBuffer.append("\t");
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        fileOutputStream.write(stringBuffer.toString().getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static void main(String[] args) throws EncoderException {
        createVideoStr("C:\\Users\\537002\\Videos\\1.mp4");
    }

    public static void createVideoStr(String path) {
        //取到视频时间
        File file = new File(path);
        Encoder encoder = new Encoder();
        MultimediaInfo info = null;
        try {
            info = encoder.getInfo(file);
            long duration = info.getDuration();
            //生成图片
            int images = createImages(path, duration, file);
            //生成字符
            File parentFile = file.getParentFile();
            String absolutePath = parentFile.getAbsolutePath() + File.separator + "temp";
            for (int i = 0; i < images; i++) {
                createAsciiPic(absolutePath+File.separator+file.getName()+i*1000+".jpg");
            }
        } catch (EncoderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int createImages(String path,long ms,File file) {
        File parentFile = file.getParentFile();
        String absolutePath = parentFile.getAbsolutePath();
        absolutePath = absolutePath + File.separator + "temp";
        File temp = new File(absolutePath);
        if (temp.exists()) {
            temp.delete();
        }
        temp.mkdir();
        long i;
        for ( i = 0; i < ms; i+=1000) {
            //按1秒的步长走
            List<String> command = new ArrayList<>();
            command.add("C:\\Users\\537002\\soft\\ffmpeg-20180508-293a6e8-win64-static\\bin\\ffmpeg.exe");
            command.add("-ss");
            //当前秒
            command.add(getVideoTime(i));
            command.add("-i");
            //文件名称
            command.add(file.getAbsolutePath());
            command.add("-y");
            command.add("-f");
            command.add("image2");
            command.add("-vframes");
            command.add("1");
            //图片名称
            command.add(absolutePath + File.separator + file.getName() + i + ".jpg");
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command);
            try {
                Process process = processBuilder.start();
                System.out.println("==>截图完成");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (int) (i / 1000);

    }

    public static String getVideoTime(long ms) {
        StringBuilder sb = new StringBuilder();
        long hour = ms / hh;
        long minute = (ms - hour * hh) / mi;
        long seconed = (ms - hour * hh - minute * mi) / ss;
        sb.append(hour);
        sb.append(":");
        sb.append(minute);
        sb.append(":");
        sb.append(seconed);
        return sb.toString();
    }
}
