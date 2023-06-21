package RansomeWare;

import java.io.File;

public interface IFileEncToStr {
    public String encrypt(File inputFile) throws Exception;
    public String decrypt(File inputFile) throws Exception;
}
