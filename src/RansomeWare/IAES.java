package RansomeWare;

import java.io.File;

public interface IAES {
    public byte []  encrypt(byte [] b) throws Exception;
    public byte []  decrypt(byte [] b) throws Exception;

}
