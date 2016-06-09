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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            final String textFromPage = PdfTextExtractor.getTextFromPage(reader, i);//Extracting the content from a particular page.

            // Find the actual page number
            //Pattern pattern = Pattern.compile("- [0-9]+ -");
            Pattern pattern = Pattern.compile("Page [0-9]+");
            Matcher matcher = pattern.matcher(textFromPage);
            int pageNumber = -1;
            if (matcher.find())
            {
                int i1 = matcher.groupCount();
                String group = matcher.group(i1);
                //String pageNumberStr = group.replaceAll("- ([0-9]+) -", "$1");
                String pageNumberStr = group.replace("Page", "");
                pageNumber = Integer.parseInt(pageNumberStr);
            }

            // Populate the map with each word
            final String[] words = StringUtils.split(textFromPage);

            for (String word : words) {
                word = this.sanatize(word);
                if (word.length() > 1 && textFromPage.contains(word)) {
                    map.put(word, pageNumber);
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

        // change "l’administration" to "adminisatration"
        int j = word.indexOf("’");
        if (j != -1) {
            word = word.substring(j+1);
        }

        word = word.replace("\"", "");
        word = word.replace(".", "");
        word = word.replace("(", "");
        word = word.replace(")", "");
        word = word.replace(",", "");
        word = word.replace(";", "");
        word = word.replace(":", "");
        word = word.replace("=", "");
        word = word.replace("«", "");
        word = word.replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");

        // if it contains only numbers
        String regex = "[0-9]+";
        if (word.matches(regex)) {
            word = StringUtils.EMPTY;
        }

        return word.trim();
    }
}
