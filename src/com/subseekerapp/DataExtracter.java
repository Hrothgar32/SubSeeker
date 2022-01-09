package com.subseekerapp;

import org.jdom2.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class DataExtracter{

    public static void decode(List<Element> subtitleFiles, ArrayList<String> names, ArrayList<String> addresses) throws Exception{
        for(int i = 0; i < subtitleFiles.size(); i++){
            Element currentFile = subtitleFiles.get(i);
            String currentName = names.get(i);
            String currentAddress = addresses.get(i);
            byte[] men =  Base64.getDecoder().decode(currentFile.getText());
            File gzOutput = new File("output.gz");
            try{
                FileOutputStream stream = new FileOutputStream(gzOutput);
                stream.write(men);
                stream.close();
            }catch (Exception ex){ex.printStackTrace();};
            FileInputStream fin = new FileInputStream(gzOutput);
            GZIPInputStream gzin = new GZIPInputStream(fin);
            File subtitleFile = new File(currentAddress, currentName);
            subtitleFile.delete();
            FileOutputStream stream = new FileOutputStream(subtitleFile);
            for(int c = gzin.read(); c != -1; c = gzin.read())
                stream.write(c);
            stream.close();
        }
    }


}