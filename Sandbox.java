import java.util.Arrays;

public class Sandbox {
    public static void main(String[] args) {
        String message = "Hello Everybody";
        String password = "Password123";

        MessageEncryptor matrix2x2 = new MessageEncryptor(password);

        System.out.println("Encryption Matrix:\n" + Arrays.deepToString(matrix2x2.getEncryptionMatrix()) + "\n");

        System.out.println("Original Message:\n" + message + "\n");

        float[][] encrypted = matrix2x2.encryptMessage(message);

        System.out.println("Encrypted Message Matrix:\n" + Arrays.deepToString(encrypted) + "\n");

        System.out.println("Decrypted Message:\n" + matrix2x2.decryptMessage(encrypted));
    }
}
