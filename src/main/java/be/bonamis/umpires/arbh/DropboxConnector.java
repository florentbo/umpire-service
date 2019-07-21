package be.bonamis.umpires.arbh;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DropboxConnector {

    private static final String LISTING_AN_PATH = "/Appointments/Listing Officials.xlsx";

    private final DbxClientV2 client;

    public DropboxConnector(String accessToken) {
        //this.accessToken = accessToken;
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        client = new DbxClientV2(config, accessToken);
    }

    public InputStream getListing(){
        try {
            DbxDownloader<FileMetadata> downloader = getFile(LISTING_AN_PATH);
            log.info("Getting listing path: " + LISTING_AN_PATH);
            InputStream inputStream = downloader.getInputStream();
            log.info("Getting listing inputStream: " + inputStream);
            return inputStream;
        } catch (Exception e) {
            log.error("problem with the DropboxConnector: Exception is: " + e);
        }
        return null;
    }

    public Date getListingModifiedDate(){
        try {
            DbxDownloader<FileMetadata> downloader = getFile(LISTING_AN_PATH);
            return downloader.getResult().getClientModified();
        } catch (Exception e) {
            log.error("problem with the DropboxConnector: Exception is: " + e);
        }
        return null;

    }

    public Set<DropboxFile> getFiles() {
        //String path = "/design_sms/2017-2018";
        String path = "/Appointments/2018-2019";
        List<Metadata> entries = getEntries(path);

        Supplier<TreeSet<DropboxFile>> supplier =
                () -> new TreeSet<>(Comparator.comparing(DropboxFile::getDate).reversed());

        return entries
                .stream()
                .filter(metadata -> metadata instanceof FolderMetadata)
                .flatMap((Function<Metadata, Stream<DropboxFile>>)
                        metadata -> getEntries(metadata.getPathLower()).stream()
                        .map(DropboxFile::new))
                .filter(s -> s.name.endsWith(".xlsx"))
                .collect(Collectors.toCollection(supplier));
    }

    public DbxDownloader<FileMetadata> getFile(String path) {
        try {
            return client.files().download(path);
        } catch (DbxException e) {
            log.error("problem with the getting the file: {}. Exception is: {}" , path, e);
        }
        return null;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class DropboxFile{
        private String name;
        private String folder;
        private Date date;

        DropboxFile(Metadata metadata) {
            this.name = metadata.getName();
            this.folder = metadata.getPathLower();
            this.date = getClientModified(metadata);
        }

        private Date getClientModified(Metadata metadata) {
            try {
                return ((FileMetadata) metadata).getClientModified();
            } catch (Exception e) {
                log.error("problem with the file: {}. Exception is: {}" , metadata, e);
            }
            return new Date();

        }
    }

    private List<Metadata> getEntries(String path)  {
        try {
            return client.files().listFolder(path).getEntries();
        } catch (DbxException e) {
            log.error("problem with the getting the list folder of the path: {}. Exception is: {}" , path, e);
        }
        return Collections.emptyList();
    }


}
