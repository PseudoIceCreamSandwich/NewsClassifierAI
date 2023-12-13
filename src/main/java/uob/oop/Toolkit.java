package uob.oop;

import org.nd4j.linalg.learning.config.Nesterovs;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Toolkit {
    public static List<String> listVocabulary = null;
    public static List<double[]> listVectors = null;
    private static final String FILENAME_GLOVE = "glove.6B.50d_Reduced.csv";

    public static final String[] STOPWORDS = {"a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"};

    public void loadGlove() throws IOException {
        BufferedReader myReader = null;
        //TODO Task 4.1 - 5 marks
        listVocabulary = new ArrayList<String>();
        listVectors = new ArrayList<double[]>();

        try{
            File file = Toolkit.getFileFromResource(FILENAME_GLOVE);
            FileReader fileReader = new FileReader(file);
            myReader = new BufferedReader(fileReader);

            String line;
            while ((line = myReader.readLine()) != null){
                String[] lineArray = line.split(",");
                listVocabulary.add(lineArray[0]);
                String[] vectorArray = new String[50];
                System.arraycopy(lineArray, 1, vectorArray, 0, 50);
                double[] doubleVectorArray = new double[50];
                for (int i = 0; i < 50; i++){
                    doubleVectorArray[i] = Double.parseDouble(vectorArray[i]);
                }
                listVectors.add(doubleVectorArray);

            }
            fileReader.close();
            myReader.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }



    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = Toolkit.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException(fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    public List<NewsArticles> loadNews() {
        List<NewsArticles> listNews = new ArrayList<>();
        //TODO Task 4.2 - 5 Marks
        File[] files = new File("src/main/resources/News").listFiles();

        try{
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".htm")) {
                    String htmlCode = Files.readString(file.toPath());
                    String title = HtmlParser.getNewsTitle(htmlCode);
                    String content = HtmlParser.getNewsContent(htmlCode);
                    NewsArticles.DataType type = HtmlParser.getDataType(htmlCode);
                    String label = HtmlParser.getLabel(htmlCode);
                    listNews.add(new NewsArticles(title, content, type, label));
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        return listNews;
    }

    public static List<String> getListVocabulary() {
        return listVocabulary;
    }

    public static List<double[]> getlistVectors() {
        return listVectors;
    }
}
