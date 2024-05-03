package ro.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OriconService {

    public static class Entry {
        private final String artist;
        private final String title;
        private final String linkUrl;
        private final String status;

        public Entry(String artist, String title, String linkUrl, String status) {
            this.artist = artist;
            this.title = title;
            this.linkUrl = linkUrl;
            this.status = status;
        }

        // Getters
        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public String getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "Artist: " + artist + ", Title: " + title + ", Link URL: " + linkUrl + ", Status: " + status;
        }
    }
    // Method to get the page list for a specific category, year, and month
    public String getPage(String type, int year, int month) throws IOException {
        String url = "https://www.oricon.co.jp/rank/RankNavigationCalendar.php?kbn=" + type +
                "&type=w&date=" + year + "-" + month + "-1&url_date=" + year + "-" + month +
                "-1&trigger=change";

        Document doc = Jsoup.connect(url).get();
        List<String> pageList = new ArrayList<>();
        for (Element option : doc.select("div.block-rank-search-box div.wrap-select-week select option")) {
            pageList.add(option.attr("value"));
        }

        // If pageList is empty and month is not January, recursively call getPage with month - 1
        if (pageList.isEmpty() && month > 1) {
            return getPage(type, year, month - 1);
        } else {
            // Otherwise, return the first element of pageList
            return pageList.isEmpty() ? null : pageList.get(0);
        }
    }

    // Method to parse a ranking page and return the rankings
    private List<Entry> parsePage(String url) throws IOException {
        List<Entry> entries = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        for (Element section : doc.select("section")) {
            Element data = section.selectFirst("div.inner");
            Element link = data.selectFirst("a");
            String linkUrl = (link != null) ? "https://www.oricon.co.jp" + link.attr("href") : "";
            String title = data.selectFirst("div h2").text();
            String artist = data.selectFirst("div p.name").text();
            String date = data.selectFirst("div ul li").text();
            Element statusElement = section.select("div.inner-label p").get(1);
            String status = statusElement.text();
            if (date.contains("2019年")) {
                continue;
            }
            entries.add(new Entry(artist, title, linkUrl, status));
        }
        return entries;
    }

    // Method to get daily singles rankings
    public List<Entry> getDailySinglesRankings() throws IOException {
        LocalDate today = LocalDate.now().minusDays(1); // Subtract one day
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String url = "https://www.oricon.co.jp/rank/js/d/" + today.format(formatter) + "/";
        Document doc = Jsoup.connect(url).get();

        List<Entry> entries = new ArrayList<>();
        Elements entryElements = doc.select("section.box-rank-entry");

        for (Element entryElem : entryElements) {
            String rank = entryElem.select("p.num").text().trim();
            String status = entryElem.select("p.status").text().trim();
            String artist = entryElem.select("p.name").text().trim();
            String title = entryElem.select("h2.title").text().trim();
            String linkUrl = "https://www.oricon.co.jp" + entryElem.select("a[itemprop=url]").attr("href");

            entries.add(new Entry(artist, title, linkUrl, status));
        }

        return entries;
    }

    // Method to get weekly singles rankings
    public List<Entry> getWeeklySinglesRankings(int year, int month) throws IOException {
        String page = getPage("cos", year, month);
        return parsePage(page);
    }

    // Method to get weekly albums rankings
    public List<Entry> getWeeklyAlbumsRankings(int year, int month) throws IOException {
        String page = getPage("coa", year, month);
        return parsePage(page);
    }

}