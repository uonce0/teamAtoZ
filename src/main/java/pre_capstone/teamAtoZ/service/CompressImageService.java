package pre_capstone.teamAtoZ.service;

import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class CompressImageService {

    // 이미지 크기 압축 메소드
    public void compressImage(BufferedImage image, File outputFile, float quality) throws IOException {
        // 이미지 형식 확인
        String fileExtension = getFileExtension(outputFile);  // 파일 확장자
        System.out.println("입력 이미지 파일 확장자: " + fileExtension);

        // 이미지가 PNG인 경우 투명도를 제거하고 JPG로 변환
        if (image.getType() == BufferedImage.TYPE_INT_ARGB || image.getType() == BufferedImage.TYPE_INT_ARGB_PRE) {
            System.out.println("JPG로 변환");
            image = removeAlphaChannel(image);
        }

        // 이미지가 RGB 형식으로 변환되었는지 확인
        if (image.getType() != BufferedImage.TYPE_INT_RGB) {
            image = convertToRGB(image);  // JPG로 저장되기 전 RGB로 변환
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
                writer.setOutput(imageOutputStream);

                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality); // 압축 품질 설정

                writer.write(null, new IIOImage(image, null, null), param);
            } finally {
                writer.dispose();
            }

            // 압축된 이미지 데이터를 파일로 저장
            ImageIO.write(image, "jpg", outputFile);
        }
    }

    // PNG 이미지에서 알파 채널 제거
    private static BufferedImage removeAlphaChannel(BufferedImage img) {
        if (!img.getColorModel().hasAlpha()) {
            return img; // 이미 알파 채널이 없으면 그대로 반환
        }

        // 알파 채널 제거 후 RGB 타입으로 변환
        BufferedImage target = createImage(img.getWidth(), img.getHeight(), false);
        Graphics2D g = target.createGraphics();

        // 흰색 배경 채우기
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return target;
    }

    // 이미지가 RGB로 강제 변환
    private static BufferedImage convertToRGB(BufferedImage img) {
        BufferedImage rgbImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rgbImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return rgbImage;
    }

    // 이미지 타입에 맞는 BufferedImage 생성 (알파 채널 여부에 따라)
    private static BufferedImage createImage(int width, int height, boolean hasAlpha) {
        return new BufferedImage(width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    }

    // 파일 확장자 추출
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int extIndex = fileName.lastIndexOf(".");
        return (extIndex == -1) ? "" : fileName.substring(extIndex + 1).toLowerCase();
    }
}