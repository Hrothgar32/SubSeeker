package com.subseekerapp;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.xpath.XPathExpression;

public abstract class MethodCaller{

    private static Document currentDocument;

    private static void SendRequest(File xmlToSend){
        File poka = xmlToSend;
        try{
            URL url = new URL("https://api.opensubtitles.org/xml-rpc");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent","SubSeeker1");
            connection.setRequestProperty("Host","https://api.opensubtitles.org/xml-rpc");
            connection.setRequestProperty("Content-Type","text/xml");
            OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
            BufferedReader gop =  new BufferedReader(new FileReader(poka));
            String a = gop.readLine();
            String k;
            try{
                while((k = gop.readLine()).isEmpty() != true)
                    a += k + '\n';
            }catch (NullPointerException ex){}
           // System.out.println(a);
            os.write(a, 0, a.length());
            os.flush();
            // reading the response
            System.out.println(connection.getResponseCode());
            InputStreamReader reader = new InputStreamReader( connection.getInputStream() );
            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[ 2048 ];
            int num;
            while ( -1 != (num=reader.read( cbuf )))
            {
                buf.append( cbuf, 0, num );
            }
            String result = buf.toString();
            System.err.println( "\nResponse from server after POST:\n" + result );
            ReceiveRequest(result);
        }catch (Exception e){e.printStackTrace();}
    }

    private static void ReceiveRequest(String currentResponse){
        try{
            SAXBuilder builder = new SAXBuilder();
            InputStream stream = new ByteArrayInputStream(currentResponse.getBytes("UTF-8"));
            currentDocument = builder.build(stream);
        }catch (Exception ex){ex.printStackTrace();}
    }

    public static String LogIn(String username, String password, String language, String useragent){
        Document logRequest = new Document();
        Element methodCall = new Element("methodCall");
        logRequest.setRootElement(methodCall);
        Element params = new Element("params");

        Element methodName = new Element("methodName");
        methodName.addContent("LogIn");
        methodCall.addContent(methodName);

        Element nameParam = new Element("param");
        Element nameVal = new Element("value");
        Element nameString = new Element("string");
        nameString.addContent(username);
        nameVal.addContent(nameString);
        nameParam.addContent(nameVal);
        params.addContent(nameParam);

        Element passParam =  new Element("param");
        Element passVal = new Element("value");
        Element passString = new Element("string");
        passString.addContent(password);
        passVal.addContent(passString);
        passParam.addContent(passVal);
        params.addContent(passParam);

        Element languageParam = new Element("param");
        Element languageVal = new Element("value");
        Element languageString = new Element("string");
        languageString.addContent(language);
        languageVal.addContent(languageString);
        languageParam.addContent(languageVal);
        params.addContent(languageParam);

        Element agentParam = new Element("param");
        Element agentVal = new Element("value");
        Element agentString = new Element("string");
        agentString.addContent(useragent);
        agentVal.addContent(agentString);
        agentParam.addContent(agentVal);
        params.addContent(agentParam);

        methodCall.addContent(params);

        try {
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            File k = new File("C:\\Users\\zolda\\IdeaProjects\\SubSeeker\\src\\com\\subseekerapp\\method.xml");
            xmlOutputter.output(logRequest, new FileOutputStream(k));
            SendRequest(k);
            return parseLoginResponse();
        }catch (Exception ex){ex.printStackTrace();}
        return null;
    }

    private static String parseLoginResponse(){
        return currentDocument.getRootElement().getChild("params").getChild("param").getChild("value").getChild("struct").getChild("member").getChild("value").getChild("string").getText();
    }

    private static String parseLogOutResponse(){
        return currentDocument.getRootElement().getChild("params").getChild("param").getChild("value").getChild("struct").getChild("member").getChild("value").getChild("string").getText();
    }

    private static void outputResponse(String whereTo){
        try{
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            File p = new File("C:\\Users\\zolda\\IdeaProjects\\SubSeeker\\src\\com\\subseekerapp\\" + whereTo);
            outputter.output(currentDocument,new FileOutputStream(p));
        }catch (IOException ex){}
    }

    private static void parseSearchResponse(MovieData movieData, SubSeekerGUI mainGUI) {
        XPathExpression<Element> imdbIDExpression = XPathFactory.instance().compile("//struct/member[30]/value/string",Filters.element());
        List<Element> imdbIDs = imdbIDExpression.evaluate(currentDocument);
        ConcurrentSkipListSet<String> uniqueIDs = new ConcurrentSkipListSet<>();
        for(Element a : imdbIDs)
            uniqueIDs.add(a.getText());
        for(String id : uniqueIDs){
            System.out.println(id + ":");
            String expression = "//struct/member[30]/value[string = '" + id + "']//ancestor::struct/member[6]/value/string";
            System.out.println(expression);
            XPathExpression<Element> subFileIDExpression = XPathFactory.instance().compile(expression,Filters.element());
            expression = "//struct/member[30]/value[string = '" + id + "']//ancestor::struct/member[7]/value/string";
            XPathExpression<Element> subFileNameExpression = XPathFactory.instance().compile(expression,Filters.element());
            List<Element> subFileIDs = subFileIDExpression.evaluate(currentDocument);
            List<Element> subFileNames = subFileNameExpression.evaluate(currentDocument);
            mainGUI.getSelectSubtitlesWindow().addMovieSelections(id,subFileIDs,subFileNames);
        }
        mainGUI.getSelectSubtitlesWindow().go();
    }

    private static void parseDownloadResponse(ArrayList<String> names, ArrayList<String> addresses){
        XPathExpression<Element> el = XPathFactory.instance().compile("//member[2]/value/string", Filters.element());
        List<Element> dataList = el.evaluate(currentDocument);
        try{
            DataExtracter.decode(dataList,names,addresses);
        }catch (Exception ex){ex.printStackTrace();}
    }

    public static String LogOut(String token){
        Document logOutRequest = new Document();
        Element methodCall = new Element("methodCall");
        logOutRequest.setRootElement(methodCall);

        Element methodName = new Element("methodName");
        methodName.addContent("LogOut");

        Element params = new Element("params");
        Element tokenParam = new Element("param");
        Element tokenValue = new Element("value");
        Element tokenString = new Element("string");
        tokenString.addContent(token);
        tokenValue.addContent(tokenString);
        tokenParam.addContent(tokenValue);
        params.addContent(tokenParam);

        methodCall.addContent(methodName);
        methodCall.addContent(params);

        try {
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            File k = new File("C:\\Users\\zolda\\IdeaProjects\\SubSeeker\\src\\com\\subseekerapp\\method.xml");
            xmlOutputter.output(logOutRequest, new FileOutputStream(k));
            SendRequest(k);
            return parseLogOutResponse();
        }catch (Exception ex){}
        return null;
    }

    public static void SearchSubtitles(String token, MovieData movieData, SubSeekerGUI mainGUI){
        ArrayList<String> languages = movieData.getMovieLangs();
        ArrayList<String> movieHashes = movieData.getMovieHashes();
        ArrayList<Long> movieByteSizes = movieData.getMovieByteSizes();

        Document searchRequest = new Document();
        Element methodCall = new Element("methodCall");
        searchRequest.setRootElement(methodCall);

        Element methodName = new Element("methodName");
        methodName.addContent("SearchSubtitles");
        methodCall.addContent(methodName);

        Element params = new Element("params");

        Element tokenParam = new Element("param");
        Element tokenValue = new Element("value");
        Element tokenString = new Element("string");
        tokenString.addContent(token);
        tokenValue.addContent(tokenString);
        tokenParam.addContent(tokenValue);
        params.addContent(tokenParam);

        Element mainParam = new Element("param");
        Element mainValue = new Element("value");
        mainParam.addContent(mainValue);
        Element arrayParam = new Element("array");
        Element dataParam = new Element("data");
        arrayParam.addContent(dataParam);
        mainValue.addContent(arrayParam);
        for(int i = 0; i < languages.size(); i++)
        {
            Element dataValue = new Element("value");
            Element structParam = new Element("struct");

            Element langMember = new Element("member");
            Element langName = new Element("name");
            langName.addContent("sublanguageid");
            langMember.addContent(langName);
            Element langValue = new Element("value");
            Element langString = new Element("string");
            langString.addContent(languages.get(i));
            langValue.addContent(langString);
            langMember.addContent(langValue);
            structParam.addContent(langMember);

            Element movieHashMember = new Element("member");
            Element movieHashName = new Element("name");
            movieHashName.addContent("moviehash");
            movieHashMember.addContent(movieHashName);
            Element movieHashValue = new Element("value");
            Element movieHashString = new Element("string");
            movieHashString.addContent(movieHashes.get(i));
            movieHashValue.addContent(movieHashString);
            movieHashMember.addContent(movieHashValue);
            structParam.addContent(movieHashMember);

            Element movieByteSizeMember = new Element("member");
            Element movieByteSizeName = new Element("name");
            movieByteSizeName.addContent("moviebytesize");
            movieByteSizeMember.addContent(movieByteSizeName);
            Element movieByteSizeValue = new Element("value");
            Element movieByteSizeDouble = new Element("double");
            movieByteSizeDouble.addContent(Long.toString(movieByteSizes.get(i)));
            movieByteSizeValue.addContent(movieByteSizeDouble);
            movieByteSizeMember.addContent(movieByteSizeValue);
            structParam.addContent(movieByteSizeMember);

            dataValue.addContent(structParam);
            dataParam.addContent(dataValue);
        }
        params.addContent(mainParam);
        methodCall.addContent(params);
        try {
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            File k = new File("C:\\Users\\zolda\\IdeaProjects\\SubSeeker\\src\\com\\subseekerapp\\method.xml");
            xmlOutputter.output(searchRequest, new FileOutputStream(k));
            SendRequest(k);
            outputResponse("search.xml");
            parseSearchResponse(movieData,mainGUI);
        }catch (Exception ex){}
    }

    public static void DownloadSubtitles(String token, MovieData movieData){

        ArrayList<String> subID = movieData.getSubtitlesToDownload();
        ArrayList<String> names = movieData.getSubtitlesNames();
        ArrayList<String> addresses = movieData.getMovieAddresses();

        Document downloadRequest = new Document();
        Element methodCall = new Element("methodCall");
        downloadRequest.setRootElement(methodCall);
        Element methodName = new Element("methodName");
        methodName.addContent("DownloadSubtitles");
        methodCall.addContent(methodName);

        Element params = new Element("params");

        Element tokenParam = new Element("param");
        Element tokenValue = new Element("value");
        Element tokenString = new Element("string");
        tokenString.addContent(token);
        tokenValue.addContent(tokenString);
        tokenParam.addContent(tokenValue);
        params.addContent(tokenParam);

        Element arrayParam = new Element("param");
        Element arrayValue = new Element("value");
        Element array = new Element("array");
        Element data = new Element("data");
        for(String k : subID){
            Element value = new Element("value");
            Element idInt = new Element("int");
            idInt.addContent(k);
            value.addContent(idInt);
            data.addContent(value);
        }
        array.addContent(data);
        arrayValue.addContent(array);
        arrayParam.addContent(arrayValue);
        params.addContent(arrayParam);
        methodCall.addContent(params);
        try {
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            File k = new File("C:\\Users\\zolda\\IdeaProjects\\SubSeeker\\src\\com\\subseekerapp\\method.xml");
            xmlOutputter.output(downloadRequest, new FileOutputStream(k));
            SendRequest(k);
            outputResponse("downloaded.xml");
            parseDownloadResponse(names,addresses);
        }catch (Exception ex){}
    }
}
