package com.pinapple.pdf;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Thibaud on 28-05-16.
 */
public class IndexCreator {
    @Parameter(names = {"--input", "-i"})
    private static String INPUT_FILE;

    public static void main(String... args) throws URISyntaxException {
        IndexCreator creator = new IndexCreator();
        new JCommander(creator, args);
        creator.createIndex(INPUT_FILE);
    }

    public void createIndex(final String inputFile) {
        try {
            PdfReader reader = new PdfReader(inputFile);
            int n = reader.getNumberOfPages();
            this.index(reader, n);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void index(final PdfReader reader, final int n) throws IOException {
        SetMultimap<String, Integer> map = HashMultimap.create();

        for (int i = 1; i <= n; i++) {
            System.out.println("Indexing page " + i + " of " + n + "...");
            String textFromPage = PdfTextExtractor.getTextFromPage(reader, i);//Extracting the content from a particular page.
            String[] words = StringUtils.split(textFromPage);

            for (String word : words) {
                word = this.sanatize(word);
                if (word.length() > 1 && textFromPage.contains(word)) {
                    map.put(word, i);
                }
            }
        }

        System.out.println(map.toString());
    }

    private String sanatize(String word) {
        word = word.toLowerCase();

        // change "l'administration" to "adminisatration"
        int i = word.indexOf("'");
        if (i != -1) {
            word = word.substring(i+1);
        }

        word = word.replace("\"", "");
        word = word.replace(".", "");
        word = word.replace("(", "");
        word = word.replace(")", "");
        word = word.replace(",", "");
        word = word.replace(";", "");
        word = word.replace(":", "");
        word = word.replace("=", "");
        word = word.replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");

        // if it contains only numbers
        String regex = "[0-9]+";
        if (word.matches(regex)) {
            word = StringUtils.EMPTY;
        }

        return word.trim();
    }

/*    private String extractTextFromAllPages(final PdfReader reader, final int n) throws IOException {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i <= n; i++) {
            String textFromPage = PdfTextExtractor.getTextFromPage(reader, i);//Extracting the content from a particular page.
            builder.append(textFromPage);
        }

        return builder.toString();
    }*/
}
