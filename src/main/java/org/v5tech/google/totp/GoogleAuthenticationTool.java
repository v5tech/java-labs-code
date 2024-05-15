package org.v5tech.google.totp;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

/**
 * Google Authenticator 验证工具类
 *
 * 需添加如下依赖：
 *
 * <dependency>
 *     <groupId>de.taimos</groupId>
 *     <artifactId>totp</artifactId>
 *     <version>1.0</version>
 * </dependency>
 * <dependency>
 *     <groupId>commons-codec</groupId>
 *     <artifactId>commons-codec</artifactId>
 *     <version>1.17.0</version>
 * </dependency>
 * <dependency>
 *     <groupId>com.google.zxing</groupId>
 *     <artifactId>javase</artifactId>
 *     <version>3.5.3</version>
 * </dependency>
 *
 */
public class GoogleAuthenticationTool {


    /**
     * 生成32位随机码
     *
     * @return
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return new Base32().encodeToString(bytes);
    }

    /**
     * 根据32位随机码生成6位OTPCode
     *
     * @param secretKey 密钥
     * @return
     */
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }


    /**
     * 生成 Google Authenticator Key Uri
     *
     * <p>Google Authenticator 规定的 Key Uri 格式: otpauth://totp/{issuer}:{account}?secret={secret}&issuer={issuer}</p>
     * <p><a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format">https://github.com/google/google-authenticator/wiki/Key-Uri-Format</a></p>
     * <p>参数需要进行 url 编码 +号需要替换成%20</p>
     *
     * @param secret  密钥 使用 generateSecretKey 方法生成
     * @param account 用户账户 如: example@domain.com
     * @param issuer  服务名称 如: Google,GitHub
     */
    public static String getGoogleAuthenticatorBarCode(String secret, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secret, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 生成二维码（文件），返回图片的base64
     *
     * @param barCode Google Authenticator Key Uri
     * @param outPath 输出地址
     * @param width   宽度
     * @param height  高度
     * @throws WriterException
     * @throws IOException
     */
    public static String createQRCode(String barCode, String outPath, int width, int height)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCode, BarcodeFormat.QR_CODE, width, height);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream bof = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", bof);
        String base64 = imageToBase64(bof.toByteArray());
        try (FileOutputStream out = new FileOutputStream(outPath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
        return base64;
    }

    /**
     * 将图片文件转换成base64字符串
     *
     * @param bytes
     */
    private static String imageToBase64(byte[] bytes) {
        return "data:image/jpeg;base64," + new BASE64Encoder().encode(bytes);
    }

    /**
     * 生成二维码URL
     *
     * @param barCode Google Authenticator Key Uri
     * @param width   宽度
     * @param height  高度
     * @return
     */
    public static String createQRCodeURL(String barCode, int width, int height) {
        String format = "https://api.qrserver.com/v1/create-qr-code/?size=%sx%s&data=%s&ecc=M&margin=10";
        return String.format(format, width, height, barCode);
    }

    /**
     * 验证码验证
     *
     * @param secretKey 密钥
     * @param otpCode   验证码
     * @return
     */
    public static boolean validateCode(String secretKey, String otpCode) {
        return getTOTPCode(secretKey).equals(otpCode);
    }

    public static void main(String[] args) throws Exception {
        String secretKey = generateSecretKey();
        secretKey = "2CISOHK2JFQFIKYSCFSBTK2C7B3OUBCZ";
        System.out.println("secretKey = " + secretKey);
        System.out.println("getTOTPCode = " + getTOTPCode(secretKey));
        String authenticatorBarCode = getGoogleAuthenticatorBarCode(secretKey, "sfeng", "SmartOps多云管理平台");
        System.out.println("authenticatorBarCode = " + authenticatorBarCode);
        System.out.println("createQRCodeURL = " + createQRCodeURL(authenticatorBarCode, 180, 180));
        createQRCode(authenticatorBarCode, "qr.png", 180, 180);
        System.out.println("validateCode = " + validateCode(secretKey, "742807"));
    }
}
