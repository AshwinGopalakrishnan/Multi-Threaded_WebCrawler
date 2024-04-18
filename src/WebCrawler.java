import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebCrawler {
    private static final Pattern LINK_PATTERN = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1");


    public static void main(String[] args) {
        String startUrl = "https://www.linkedin.com/feed/";
        int maxDepth = promptForInteger("Enter the maximum depth: ");
        int maxLinks = promptForInteger("Enter the maximum number of links to crawl: ");
        Set<String> visitedUrls = new HashSet<>();


        crawl(startUrl, maxDepth, maxLinks, visitedUrls, 0);
    }


    private static int promptForInteger(String message) {
        int value = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean validInput = false;
        while (!validInput) {
            System.out.print(message);
            try {
                value = Integer.parseInt(reader.readLine());
                validInput = true;
            } catch (NumberFormatException | IOException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
        return value;
    }


    private static void crawl(String url, int maxDepth, int maxLinks, Set<String> visitedUrls, int depth) {
        if (depth > maxDepth || visitedUrls.size() >= maxLinks || visitedUrls.contains(url)) {
            return;
        }


        visitedUrls.add(url);
        System.out.println("Crawling: " + url);


        try {
            URL urlObj = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlObj.openStream()));
            String line;
            int linksCount = 0;
            while ((line = reader.readLine()) != null && linksCount < maxLinks) {
                Matcher matcher = LINK_PATTERN.matcher(line);
                while (matcher.find() && linksCount < maxLinks) {
                    String nextUrl = matcher.group(2);
                    if (!nextUrl.startsWith("http")) {
                        nextUrl = urlObj.getProtocol() + "://" + urlObj.getHost() + nextUrl;
                    }
                    crawl(nextUrl, maxDepth, maxLinks, visitedUrls, depth + 1);
                    linksCount++;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Failed to crawl: " + url);
            e.printStackTrace();
        }
    }
}
