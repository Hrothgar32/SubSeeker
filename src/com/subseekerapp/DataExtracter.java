package com.subseekerapp;

import org.jdom2.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

//TODO: Implementing address- and namebased file saving.

public class DataExtracter{

    public static void decode(List<Element> alap, ArrayList<String> names, ArrayList<String> addresses) throws Exception{
        for(Element curEl : alap){
            byte[] men =  Base64.getDecoder().decode(curEl.getText());
            File gzOutput = new File("C:\\Users\\zolda\\IdeaProjects\\SubSeeker\\src\\com\\subseekerapp\\output.gz");
            try{
                FileOutputStream stream = new FileOutputStream(gzOutput);
                stream.write(men);
                stream.close();
            }catch (Exception ex){ex.printStackTrace();};
            FileInputStream fin = new FileInputStream(gzOutput);
            GZIPInputStream gzin = new GZIPInputStream(fin);
            File subtitleFile = new File("C:\\Users\\zolda\\IdeaProjects\\SubSeeker\\felirat.srt");
            subtitleFile.delete();
            FileOutputStream stream = new FileOutputStream(subtitleFile);
            for(int c = gzin.read(); c != -1; c = gzin.read())
                stream.write(c);
            stream.close();
        }
    }


}