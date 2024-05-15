package org.v5tech.google.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import static com.warrenstrange.googleauth.GoogleAuthenticator.SCRATCH_CODE_MODULUS;

public class GoogleAuthentication2 {

    static GoogleAuthenticator authenticator = new GoogleAuthenticator();

    static {
        authenticator.setCredentialRepository(new CredentialRepository());
    }

    public static String userName = "fengj";
    public static String issuer = "SmartOps 多云管理平台";

    public static void main(String[] args) {
        String secretKey = getSecretKey(userName);
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, userName, new GoogleAuthenticatorKey.Builder(secretKey).build());
        System.out.println("otpAuthURL = " + otpAuthURL);
        int totpCode = getTotpCode(secretKey);
        verifyCode(userName, totpCode);
    }

    public static String getSecretKey(String userName) {
        final GoogleAuthenticatorKey key = authenticator.createCredentials(userName);
        String secretKey = key.getKey();
        System.out.println("secretKey: " + secretKey);
        // 获取备用码
        // for (Integer code : key.getScratchCodes()) {
        //     System.out.println("Scratch Code: " + code);
        //     boolean validated = validateScratchCode(code);
        //     System.out.println("Scratch Code is " + (validated ? "valid" : "invalid"));
        // }
        return secretKey;
    }

    private static boolean validateScratchCode(Integer scratchCode) {
        return (scratchCode >= SCRATCH_CODE_MODULUS / 10);
    }

    public static void verifyCode(String userName, int totpCode) {
        // 验证密码
        boolean isValid = authenticator.authorizeUser(userName, totpCode);
        System.out.println("verifyCode = " + isValid);
    }

    public static int getTotpCode(String secretKey) {
        // 生成 TOTP 密码
        int code = authenticator.getTotpPassword(secretKey);
        System.out.println("TOTP Code: " + code);
        return code;
    }

}
