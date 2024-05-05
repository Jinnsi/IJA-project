package Downloads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomFileReader {
    private String fileName;

    public RoomFileReader(String fileName) {
        this.fileName = fileName;
    }

    public String[] readWordsFromFile() throws IOException {
        List<String> wordsList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line into words based on whitespace
                String[] wordsInLine = line.split("\\s+");
                for (String word : wordsInLine) {
                    // Add each word to the list
                    wordsList.add(word);
                }
            }
        }

        // Convert the list to an array
        String[] wordsArray = new String[wordsList.size()];
        wordsList.toArray(wordsArray);

        return wordsArray;
    }

    public static void main(String[] args) {
        String fileName = "room.txt";
        RoomFileReader fileReader = new RoomFileReader(fileName);
        try {
            String[] words = fileReader.readWordsFromFile();
            // Print the words to verify
            for (String word : words) {
                System.out.println(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

