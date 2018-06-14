package bean;

/**
 * Created by ltaoj on 2018/06/04 20:24.
 *
 * @version : 1.0
 */
public class LexicalKV {

    private String word;

    private String type;

    public LexicalKV(String word, String type) {
        this.word = word;
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "word: " + word + " -> type: " + type + "\n";
    }
}
