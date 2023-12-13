package uob.oop;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.Properties;


public class ArticlesEmbedding extends NewsArticles {
    private int intSize = -1;
    private String processedText = "";

    private INDArray newsEmbedding = Nd4j.create(0);

    public ArticlesEmbedding(String _title, String _content, NewsArticles.DataType _type, String _label) {
        //TODO Task 5.1 - 1 Mark
        super(_title, _content, _type, _label);
    }

    public void setEmbeddingSize(int _size) {
        //TODO Task 5.2 - 0.5 Marks
        intSize = _size;

    }

    public int getEmbeddingSize(){
        return intSize;
    }

    @Override
    public String getNewsContent() {
        //TODO Task 5.3 - 10 Marks

        if (!processedText.isEmpty()){
            return processedText.trim();
        }
        else {
            //Text cleaning
            processedText = textCleaning(super.getNewsContent());


            //Text lemmatization
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,pos,lemma");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            CoreDocument document = pipeline.processToCoreDocument(processedText);
            StringBuilder builder = new StringBuilder();
            for (CoreLabel tok : document.tokens()) {
                builder.append(tok.lemma());
                builder.append(" ");
            }
            processedText = builder.toString().toLowerCase();

            //Stop words removal
            String[] stopWords = Toolkit.STOPWORDS;
            StringBuilder stopWordsStringBuilder = new StringBuilder();
            for (int i = 0; i < stopWords.length; i++){
                stopWordsStringBuilder.append(stopWords[i]).append(" ");
            }
            String stopWordsString = stopWordsStringBuilder.toString().trim();
            String[] textArray = processedText.split(" ");
            builder.setLength(0);
            outerLoop:
            for (String s : textArray) {

                for (String stopWord : stopWords) {
                    if (s.equals(stopWord)) {
                        continue outerLoop; // Skip to the next word if it's a stop word
                    }
                }

                builder.append(s).append(" ");

            }

            processedText = builder.toString();


        }


        return processedText.trim();
    }

    public INDArray getEmbedding() throws Exception {
        //TODO Task 5.4 - 20 Marks

        if (intSize == -1){
            throw new InvalidSizeException("Invalid size");
        }

        if (processedText.isEmpty()){
            throw new InvalidTextException("Invalid text");
        }


        if (newsEmbedding.isEmpty()){
            Glove[] gloveArray = new Glove[AdvancedNewsClassifier.listGlove.size()];
            for (int i = 0; i < AdvancedNewsClassifier.listGlove.size(); i++){
                gloveArray[i] = AdvancedNewsClassifier.listGlove.get(i);
            }
            String[] content = processedText.split(" ");

            int embeddingSize = Math.min(intSize, content.length);
            newsEmbedding = Nd4j.zeros(intSize, gloveArray[0].getVector().getVectorSize());

            int count = 0;
            for (String word : content){
                for (Glove glove : gloveArray){
                    if (word.equals(glove.getVocabulary())){
                        INDArray vectors = Nd4j.create(glove.getVector().getAllElements());
                        newsEmbedding.putRow(count++, vectors);
                        break;
                    }
                }
                if (count >= embeddingSize){
                    break;
                }
            }
        }


        return Nd4j.vstack(newsEmbedding.mean(1));
    }

    /***
     * Clean the given (_content) text by removing all the characters that are not 'a'-'z', '0'-'9' and white space.
     * @param _content Text that need to be cleaned.
     * @return The cleaned text.
     */
    private static String textCleaning(String _content) {
        StringBuilder sbContent = new StringBuilder();

        for (char c : _content.toLowerCase().toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || Character.isWhitespace(c)) {
                sbContent.append(c);
            }
        }

        return sbContent.toString().trim();
    }
}
