package be.bonamis.umpires.business;

import be.bonamis.umpires.arbh.DropboxConnector;
import be.bonamis.umpires.arbh.DropboxConnector.DropboxFile;
import be.bonamis.umpires.arbh.Listing;
import be.bonamis.umpires.arbh.Listing.Umpire;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class ColleagueService {

    private final DropboxConnector dropboxConnector;
    private Collection<Umpire> umpires;
    private LocalDateTime modifiedDate;

    public ColleagueService(String accessToken) {
        this.dropboxConnector = new DropboxConnector(accessToken);
        updateUmpires();
    }

    private void updateUmpires() {
        this.umpires = new Listing(this.dropboxConnector.getListing()).getUmpires();
        this.modifiedDate = convertToLocalDateTimeViaInstant(this.dropboxConnector.getListingModifiedDate());
    }

    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Collection<Colleague> getColleagues() {
        return umpires.stream().map(ColleagueService.Colleague::new).collect(toSet());
    }

    String getPhone(String name) {
        return umpires
                .stream()
                .map(ColleagueService.Colleague::new)
                .filter(getColleaguePredicate(name))
                .findFirst()
                .map(colleague -> colleague.phone)
                .orElse(null);
    }

    private Predicate<Colleague> getColleaguePredicate(String name) {
        return colleague -> {
            boolean equals = colleague.fullName.equals(name);
            return equals || clean(colleague.fullName).equals(clean(name));
        };
    }

    static String clean (String src){
        return flattenToAscii(src
                .replace("'" ,"")
                .replace("," ,"")
                .replace(" " ,""))
                .toLowerCase();
    }

    private static String flattenToAscii(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        for (char c : Normalizer.normalize(string, Normalizer.Form.NFD).toCharArray()) {
            if (c <= '\u007F') sb.append(c);
        }
        return sb.toString();
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public Set<DropboxFile> getFiles() {
        return dropboxConnector.getFiles();
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    void getNewUmpires() {
        LocalDateTime listingModifiedDate = convertToLocalDateTimeViaInstant(this.dropboxConnector.getListingModifiedDate());
        if (listingModifiedDate.isAfter(this.modifiedDate)) {

            log.info("Get new umpires at: {}", dateFormat.format(new Date()));
            updateUmpires();
        }
    }

    InputStream getFile(String path) {
        return dropboxConnector.getFile(path).getInputStream();
    }

    @Data
    @AllArgsConstructor
    public static class Colleague {
        private String fullName;
        private String phone;
        private String email;

        Colleague(Umpire umpire) {
            this.fullName = umpire.getName() + " " + umpire.getFirst();
            this.phone = umpire.getMobile();
            this.email = umpire.getEMail();
        }
    }
}
