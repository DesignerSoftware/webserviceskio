/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.kiosko.generales;



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author UPC007
 */
public class Encriptacion {
    private static SecretKeySpec secretKey;
    private static byte[] key;

    /*public static void main(String[] args) {
        String text = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3OTgwMzEwMyIsImlhdCI6MTYzMzQ2MjE5NywiZXhwIjoxNjM2MDU3Nzk3LCJpc3MiOiJodHRwczovL3d3dy5kZXNpZ25lci5jb20uY28iLCJlbXByZXNhIjoiODYwMDA1MTE0IiwiZG9jdW1lbnRvIjoiNzk4MDMxMDMiLCJjYWRlbmEiOiJERUZBVUxUMSIsImdydXBvIjoiR3J1cG9FbXByZXNhcmlhbDEifQ.69h0uCMGal5K5CoQNS9RVOIyLNH7WClmvyF6mPb5Y0Q";
        String prueba1 = encrypt(text, "Manager01");
        //String prueba2 = decrypt(prueba1, "Manager01");

        //System.out.println("Cadena Original: " + text);
        //System.out.println("Escriptado     : " + prueba1);
        //System.out.println("Desencriptado  : " + prueba2);
        try {
            System.out.println("Cadena Original: " + text);
            System.out.println("Escriptado     : " + prueba1);
            String hex = convertStringToHex(prueba1);
            System.out.println("convertStringToHex     : " + hex);
            String str = convertHexToString(hex);
            System.out.println("convertHexToString     : " + str);
            String prueba2 = decrypt(str, "Manager01");
            System.out.println("Desencriptado  : " + prueba2);
            
        } catch (Exception e) {
        }

        ///sdnaodisnasd

        /*
        ToGv7n61bHWgrlRgz5ZYXQ==
        thalia
        CD7ZtN83LgDq9f8Fpd9fhA==
         */
    //}

    private static String convertStringToHex(String str) {
        StringBuilder stringBuilder = new StringBuilder();

        char[] charArray = str.toCharArray();

        for (char c : charArray) {
            String charToHex = Integer.toHexString(c);
            stringBuilder.append(charToHex);
        }

        //System.out.println("Converted Hex from String: " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private static String convertHexToString(String hex) {
        String str = "";
        for (int i = 0; i < hex.length(); i += 2) {
            String s = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(s, 16);
            str = str + (char) decimal;
        }
        return str;
    }

    public static String encrypt(String strToEncrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            System.out.println("Cipher" + cipher);
            String enc = Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            enc = convertStringToHex(enc);
            return enc;
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            String denc = convertHexToString(strToDecrypt);;
            denc = new String(cipher.doFinal(Base64.getDecoder().decode(denc)));
            return denc;
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
            return "N";
        }
    }
    public static void setKey(String myKey) 
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            secretKey = new SecretKeySpec(key, "AES");
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
}
