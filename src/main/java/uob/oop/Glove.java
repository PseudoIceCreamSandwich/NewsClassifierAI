package uob.oop;

public class Glove {
    private String strVocabulary;
    private Vector vecVector;

    public Glove(String _vocabulary, Vector _vector) {
        //TODO Task 1.1 - 0.5 marks
        strVocabulary = _vocabulary;
        vecVector = _vector;

    }

    public String getVocabulary() {
        //TODO Task 1.2 - 0.5 marks


        return strVocabulary; //Please modify the return value.
    }

    public Vector getVector() {
        //TODO Task 1.3 - 0.5 marks
        return vecVector;
    }

    public void setVocabulary(String _vocabulary) {
        //TODO Task 1.4 - 0.5 marks

        strVocabulary = _vocabulary;
    }

    public void setVector(Vector _vector) {
        //TODO Task 1.5 - 0.5 marks

        vecVector = _vector;
    }
}
