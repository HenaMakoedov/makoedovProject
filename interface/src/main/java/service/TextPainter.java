package service;

import constants.PathsConstants;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Makoiedov.H on 11/15/2017.
 */
public class TextPainter {
    private static List<String> reservedWords;

    /**
     * Static block loads reserved words into array list
     */
    static {
        reservedWords = new ArrayList<>();
        File file = new File(PathsConstants.DEFAULT_RESERVED_WORD_FILE);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            String word = null;
            try {
                word = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (word == null || word.equals("")) {
                break;
            } else {
                reservedWords.add(word);
            }
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method returns array of colored words
     * @param text
     * @return colored words
     */
    public static Text[] paintText(String text) {
        List<Text> list = new ArrayList<>();
        String[] words = text.split("[\t ]");
        for(int i = 0; i < words.length; i++) {
            Text paintedText = new Text(words[i]);
             if (isNameWord(words[i])) {
                 paintedText.setFill(Color.GREEN);
             } else if (isKeyWord(words[i])) {
                paintedText.setFill(Color.BLUE);
            } else if (isDelimiter(words[i])) {
                paintedText.setFill(Color.RED);
            } else {
                paintedText.setFill(Color.BLACK);
            }
            paintedText.setText(paintedText.getText() + " ");
            paintedText.setFont(Font.font("System", 14));
            list.add(paintedText);
        }
        Text[] array = new Text[list.size()];
        for(int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * Method returns array of default-color words
     * @param text
     * @return default-color words
     */
    public static Text[] defaultText(String text) {
        List<Text> list = new ArrayList<>();
        String[] words = text.split("[\t ]");
        for(int i = 0; i < words.length; i++) {
            Text paintedText = new Text(words[i]);
            paintedText.setFill(Color.BLACK);
            paintedText.setText(paintedText.getText() + " ");
            paintedText.setFont(Font.font("System", 14));
            list.add(paintedText);
        }
        Text[] array = new Text[list.size()];
        for(int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * Method checks whether the word is key
     * @param word
     * @return word is key
     */
    private static boolean isKeyWord(String word) {
        for(int i = 0; i < reservedWords.size(); i++) {
            if (word.toLowerCase().startsWith(reservedWords.get(i).toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method checks whether the word is name of entity
     * @param word
     * @return word is name of entity
     */
    private static boolean isNameWord(String word) {
        return word.contains("`") || word.contains("'") || word.contains("\"");
    }

    /**
     * Method checks whether the word is delimiter
     * @param word
     * @return word is delimiter
     */
    private static boolean isDelimiter(String word) {
        return word.equals("DELIMITER") || word.equals("$$");
    }
}
