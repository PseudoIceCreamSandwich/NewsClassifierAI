package uob.oop;

public class NewsArticles {
    public enum DataType {
        Training, Testing
    }

    private String newsTitle = "", newsContent = "";
    private DataType newsType = DataType.Testing;
    private String newsLabel = "-1";

    public NewsArticles(String _title, String _content, DataType _type, String _label) {
        //TODO Task 2.1 - 0.5 marks

        newsTitle = _title;
        newsContent = _content;
        newsType = _type;
        newsLabel = _label;

    }

    public String getNewsLabel() {
        //TODO Task 2.2 - 0.5 marks
        return newsLabel; //Please modify the return value.
    }

    public DataType getNewsType() {
        //TODO Task 2.3 - 0.5 marks
        return newsType; //Please modify the return value.
    }

    public String getNewsTitle() {
        //TODO Task 2.4 - 0.5 marks
        return newsTitle; //Please modify the return value.
    }

    public String getNewsContent() {
        //TODO Task 2.5 - 0.5 marks
        return newsContent; //Please modify the return value.
    }

    public void setNewsLabel(String _lable) {
        //TODO Task 2.6 - 0.5 marks

        newsLabel = _lable;

    }

    public void setNewsType (DataType _type){
        //TODO Task 2.7 - 0.5

        newsType = _type;

    }
}
