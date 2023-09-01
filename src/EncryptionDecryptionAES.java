import java.io.File;
import java.io.FileWriter;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionDecryptionAES {
    static Cipher cipher;

    /**
     * This method determines whether the incoming text needs to be
     * encrypted or decrypted
     * 
     * @param data This is the data string that needs to be encrypted/decrypted
     * @param mode This is the mode which denotes whether to encrypt of decrypt
     * @return String This will return a string of encrypted/decrypted data
     */
    public static String encryptDecrypt(String data, String mode) {
        String textData = new String();

        try {
            File fileObj = new File("./aesKey.env");
            if (fileObj.createNewFile()) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128);
                SecretKey secretKey = keyGenerator.generateKey();
                cipher = Cipher.getInstance("AES");

                FileWriter fileWriter = new FileWriter("aesKey.env", false);
                String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

                fileWriter.write(encodedKey);
                fileWriter.close();

                if (mode.equals("encrypt")) {
                    textData = encrypt(data, secretKey);
                } else if (mode.equals("decrypt")) {
                    textData = decrypt(data, secretKey);
                }
            } else {
                Scanner myReader = new Scanner(fileObj);
                String encodedKey = new String();
                while (myReader.hasNextLine()) {
                    encodedKey = myReader.nextLine();
                    if (!encodedKey.equalsIgnoreCase("")) {
                        break;
                    }
                }
                if (!encodedKey.equals("")) {
                    byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
                    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                    if (mode.equals("encrypt")) {
                        textData = encrypt(data, originalKey);
                    } else if (mode.equals("decrypt")) {
                        textData = decrypt(data, originalKey);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while encrypting data!");
        }
        return textData;
    }

    /**
     * This method will encrypt the incoming text using AES algorithm
     * 
     * @param plainText This is the plain text string of data to be encrypted
     * @param secretKey This is the secret key generated via AES KeyGenerator
     * @return String This will return a encrypted data string
     */
    public static String encrypt(String plainText, SecretKey secretKey) {
        String encryptedText = new String();
        try {
            /*
             * Code adapted from stack overflow example
             * Available:
             * https://stackoverflow.com/questions/34121787/how-to-encrypt-decrypt-text-in-a
             * -file-in-java
             */

            byte[] plainTextByte = plainText.getBytes();
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedByte = cipher.doFinal(plainTextByte);
            Base64.Encoder encoder = Base64.getEncoder();
            encryptedText = encoder.encodeToString(encryptedByte);
        } catch (Exception e) {
            System.out.println("Could not encrypt data");
        }
        return encryptedText;
    }

    /**
     * This method will decrypt the incoming text
     * 
     * @param encryptedText This is the encrypted text string of data to be
     *                      decrypted
     * @param secretKey     This is the secret key generated via AES KeyGenerator
     * @return String This will return a decrypted data string
     */
    public static String decrypt(String encryptedText, SecretKey secretKey) {
        String decryptedText = new String();
        try {
            /*
             * Code adapted from stack overflow example
             * Available:
             * https://stackoverflow.com/questions/34121787/how-to-encrypt-decrypt-text-in-a
             * -file-in-java
             */

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] encryptedTextByte = decoder.decode(encryptedText);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
            decryptedText = new String(decryptedByte);
        } catch (Exception e) {
            System.out.println("Could not decrypt data");
        }
        return decryptedText;
    }
}