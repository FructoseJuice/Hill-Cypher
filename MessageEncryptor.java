import java.util.Arrays;
import java.util.Random;

public class MessageEncryptor {
    private float[][] encryptionMatrix = new float[2][2];

    //Initializes a random 2x2 Encryption Matrix
    public MessageEncryptor() {
        Random rand = new Random();

        for ( int i = 0; i < 2; i++ ) {
            for ( int j = 0; j < 2; j++ ) {
                encryptionMatrix[i][j] = rand.nextFloat() * 100;
            }
        }
    }
    //Initialize a random 2x2 Encryption matrix with the password's hash as the seed
    public MessageEncryptor(String password) {
        Random rand = new Random(hash(password));
        float[][] encryptionMatrix = new float[2][2];

        for ( int i = 0; i < 2; i++) {
            for ( int j = 0; j < 2; j++) {
                encryptionMatrix[i][j] = rand.nextFloat() * 100;
            }
        }

        this.encryptionMatrix = encryptionMatrix;
    }

    //Hashes a string s
    private long hash(String s) {
        long hash = 0;

        for (char c : s.toCharArray()) {
            hash = 31L*hash + c;
        }

        return hash;
    }

    public float[][] encryptMessage(String message) {
        //Initialize 2xN matrix, where N is Ceil(message.length / 2)
        float[][] messageMatrix = new float[2][(int) Math.ceil(message.length() / 2.0)];

        //Fill matrix with messages ascii values
        for (int i = 0; i < message.length(); i++ ) {
            messageMatrix[i % 2][Math.floorDiv(i, 2)] = message.charAt(i);
        }

        System.out.println("Message Matrix:\n" + Arrays.deepToString(messageMatrix) + "\n");

        //Return encrypted message matrix
        return multiplyMatrices(encryptionMatrix, messageMatrix);
    }

    //@encryptedMessage a 2xN sized matrix
    public String decryptMessage(float[][] encryptedMessage) {
        float[][] decryptedMessageMatrix;
        float[][] encryptionMatrixInverse = getInvertedMatrix();

        //Decrypt message
        decryptedMessageMatrix = multiplyMatrices(encryptionMatrixInverse, encryptedMessage);

        StringBuilder decryptedMessage = new StringBuilder();
        //Build message using StringBuilder
        for (int i = 0; i < decryptedMessageMatrix[0].length; i++ ) {
            for (int j = 0; j < 2; j++ ) {
                decryptedMessage.append((char) decryptedMessageMatrix[j][i]);
            }
        }

        //Remove possible null element at tail
        if (decryptedMessage.charAt(decryptedMessage.length() - 1) == 0x00) {
            decryptedMessage.deleteCharAt(decryptedMessage.length() - 1);
        }

        return decryptedMessage.toString();
    }

    private float dotProduct(float[] u, float[] v) {
        float dotProduct = 0;

        //Vectors u and v must both be of length n
        for (int i = 0; i < u.length; i++) {
            dotProduct += u[i] * v[i];
        }

        return dotProduct;
    }

    private float[][] multiplyMatrices(float[][] A, float[][] B) {
        final int A_ROW_COUNT = A.length;
        final int B_ROW_COUNT = B.length;
        final int B_COL_COUNT = B[0].length;

        //A is size mxn, B is size nxp
        //Let C be of size mxp
        float[][] C = new float[A_ROW_COUNT][B_COL_COUNT];

        for (int i = 0; i < A_ROW_COUNT; i++ ) {
            for (int j = 0; j < B_COL_COUNT; j++) {
                /*
                C[i][j] = dotProduct(Row i of A, Column j of B)
                 */

                //Get each column of the encrypted matrix
                float[] B_col_j = new float[B_ROW_COUNT];

                for(int c = 0; c < B_ROW_COUNT; c++) {
                    B_col_j[c] = B[c][j];
                }

                //Compute dot product of A row i, and B col j
                float dotProduct = Math.round(dotProduct(A[i], B_col_j));

                C[i][j] = dotProduct;
            }
        }

        return C;
    }

    private float[][] getInvertedMatrix() {
        float[][] invertedMatrix = encryptionMatrix;

        // 1 / (ad - bc)
        final float determinantReciprocal = 1 /
                ( (invertedMatrix[0][0] * invertedMatrix[1][1]) - (invertedMatrix[0][1] * invertedMatrix[1][0]));

        /* Calculate Matrix Adjoint
        ( d -b )
        ( -c a )
         */

        //Swap a and d
        float swap = invertedMatrix[0][0];
        invertedMatrix[0][0] = invertedMatrix[1][1];
        invertedMatrix[1][1] = swap;

        //Invert sign of b and c
        invertedMatrix[0][1] *= -1;
        invertedMatrix[1][0] *= -1;

        //Entry-wise multiply by Reciprocal of Determinant
        for (int i = 0; i < 2; i++ ) {
            for (int j = 0; j < 2; j++ ) {
                invertedMatrix[i][j] *= determinantReciprocal;
            }
        }

        return invertedMatrix;
    }

    public float[][] getEncryptionMatrix() {
        return encryptionMatrix;
    }
}
