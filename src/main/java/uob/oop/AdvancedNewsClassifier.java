package uob.oop;

import org.apache.commons.lang3.time.StopWatch;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdvancedNewsClassifier {
    public Toolkit myTK = null;
    public static List<NewsArticles> listNews = null;
    public static List<Glove> listGlove = null;
    public List<ArticlesEmbedding> listEmbedding = null;
    public MultiLayerNetwork myNeuralNetwork = null;

    public final int BATCHSIZE = 10;

    public int embeddingSize = 0;
    private static StopWatch mySW = new StopWatch();

    public AdvancedNewsClassifier() throws IOException {
        myTK = new Toolkit();
        myTK.loadGlove();
        listNews = myTK.loadNews();
        listGlove = createGloveList();
        listEmbedding = loadData();

    }

    public static void main(String[] args) throws Exception {
        mySW.start();
        AdvancedNewsClassifier myANC = new AdvancedNewsClassifier();

        myANC.embeddingSize = myANC.calculateEmbeddingSize(myANC.listEmbedding);
        myANC.populateEmbedding();
        myANC.myNeuralNetwork = myANC.buildNeuralNetwork(2);
        myANC.predictResult(myANC.listEmbedding);
        myANC.printResults();
        mySW.stop();
        System.out.println("Total elapsed time: " + mySW.getTime());
    }

    public List<Glove> createGloveList() {
        List<Glove> listResult = new ArrayList<>();
        //TODO Task 6.1 - 5 Marks

        List<String> listVocabulary = Toolkit.getListVocabulary();
        List<double[]> listVectors = Toolkit.getlistVectors();
        String[] stopWords = Toolkit.STOPWORDS;

        outerloop:
        for (int i = 0; i < listVocabulary.size(); i++){
            for (String stopWord : stopWords) {
                if (stopWord.equals(listVocabulary.get(i))) {
                    continue outerloop;
                }
            }
            listResult.add(new Glove(listVocabulary.get(i), new Vector(listVectors.get(i))));
        }

        return listResult;
    }


    public static List<ArticlesEmbedding> loadData() {
        List<ArticlesEmbedding> listEmbedding = new ArrayList<>();
        for (NewsArticles news : listNews) {
            ArticlesEmbedding myAE = new ArticlesEmbedding(news.getNewsTitle(), news.getNewsContent(), news.getNewsType(), news.getNewsLabel());
            listEmbedding.add(myAE);
        }
        return listEmbedding;
    }

    public int calculateEmbeddingSize(List<ArticlesEmbedding> _listEmbedding) {
        int intMedian = -1;
        //TODO Task 6.2 - 5 Marks

        String[] stopWords = Toolkit.STOPWORDS;

        List<Integer> words = new ArrayList<>();
        int len;

        for (ArticlesEmbedding articlesEmbedding : _listEmbedding) {
            String[] articleWords = articlesEmbedding.getNewsContent().split(" ");

            len = 0;
            for (String articleWord : articleWords) {
                for (Glove glove : listGlove) {
                    if (articleWord.equals(glove.getVocabulary())) {
                        len += 1;
                        break;
                    }
                }

            }


            words.add(len);
        }


        for (int i = 0; i < words.size(); i++) {

            for (int j = i + 1; j < words.size(); j++) {

                int temp = 0;
                if (words.get(j) < words.get(i)) {


                    temp = words.get(i);
                    words.set(i, words.get(j));
                    words.set(j, temp);
                }
            }

        }

        if (words.size() % 2 == 0){
            int first = words.get(words.size() / 2);
            int second = words.get((words.size() / 2) + 1);
            intMedian = ((first + second) / 2);
        }
        else {
            intMedian = words.get((words.size() + 1) / 2);
        }
        return intMedian;
    }

    public void populateEmbedding() {
        //TODO Task 6.3 - 10 Marks

        for (ArticlesEmbedding articlesEmbedding : listEmbedding){
            boolean fine = false;
            while(!fine){
                try{
                    articlesEmbedding.getEmbedding();
                    fine = true;
                }
                catch (InvalidSizeException e){
                    articlesEmbedding.setEmbeddingSize(embeddingSize);
                }
                catch (InvalidTextException e){
                    articlesEmbedding.getNewsContent();
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }

    }

    public DataSetIterator populateRecordReaders(int _numberOfClasses) throws Exception {
        ListDataSetIterator myDataIterator = null;
        List<DataSet> listDS = new ArrayList<>();
        INDArray inputNDArray = null;
        INDArray outputNDArray = null;

        //TODO Task 6.4 - 8 Marks
        for (ArticlesEmbedding articlesEmbedding : listEmbedding) {
            if (articlesEmbedding.getNewsType() == NewsArticles.DataType.Training) {
                inputNDArray = articlesEmbedding.getEmbedding();
                outputNDArray = Nd4j.zeros(1, _numberOfClasses);
                int label = Integer.parseInt(articlesEmbedding.getNewsLabel());
                outputNDArray.putScalar(new int[]{0, label - 1}, 1);
                DataSet dataSet = new DataSet(inputNDArray, outputNDArray);
                listDS.add(dataSet);
            }
        }


        return new ListDataSetIterator(listDS, BATCHSIZE);
    }

    public MultiLayerNetwork buildNeuralNetwork(int _numOfClasses) throws Exception {
        DataSetIterator trainIter = populateRecordReaders(_numOfClasses);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(42)
                .trainingWorkspaceMode(WorkspaceMode.ENABLED)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(Adam.builder().learningRate(0.02).beta1(0.9).beta2(0.999).build())
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(embeddingSize).nOut(15)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.HINGE)
                        .activation(Activation.SOFTMAX)
                        .nIn(15).nOut(_numOfClasses).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        for (int n = 0; n < 100; n++) {
            model.fit(trainIter);
            trainIter.reset();
        }
        return model;
    }

    public List<Integer> predictResult(List<ArticlesEmbedding> _listEmbedding) throws Exception {
        List<Integer> listResult = new ArrayList<>();
        //TODO Task 6.5 - 8 Marks

        for (ArticlesEmbedding articlesEmbedding : _listEmbedding) {
            if (articlesEmbedding.getNewsType() == NewsArticles.DataType.Testing) {
                int prediction = myNeuralNetwork.predict(articlesEmbedding.getEmbedding())[0];
                listResult.add(prediction);
                articlesEmbedding.setNewsLabel(Integer.toString(prediction + 1));
            }
        }


        return listResult;
    }

    public void printResults() {
        //TODO Task 6.6 - 6.5 Marks

        List<ArrayList<String>> titles = new ArrayList<>();

        for (int i = 0; i < myNeuralNetwork.getLabels().shape()[1]; i++){
            titles.add(new ArrayList<>());
        }

        for (ArticlesEmbedding articlesEmbedding : listEmbedding){
            if (articlesEmbedding.getNewsType() == NewsArticles.DataType.Testing){
                int label = Integer.parseInt(articlesEmbedding.getNewsLabel());

                titles.get(label - 1).add(articlesEmbedding.getNewsTitle());
            }
        }

        for (int i = 0; i < titles.size(); i++){
            System.out.print("Group " + (i + 1) + "\r\n");
            for (int j = 0; j < titles.get(i).size(); j++){
                System.out.print(titles.get(i).get(j) + "\r\n");
            }
        }

        
    }
}
