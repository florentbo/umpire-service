/*
package be.bonamis.umpires.arbh;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DropboxConnectorTest {

    private DropboxConnector dropboxConnector;
    private static final String ACCESS_TOKEN = "TO BE FILLED";

    @Before
    public void setUp(){
        dropboxConnector = new DropboxConnector(ACCESS_TOKEN);
    }

    @Test
    public void getListingModifiedDate()  {
        assertThat(dropboxConnector.getListingModifiedDate()).isBeforeYear(2020).isAfterYear(2017);
    }

    @Test
    public void getFiles()  {
        assertThat(dropboxConnector.getFiles().size()).isGreaterThan(4);
    }

    @Test
    public void getFile() throws UnsupportedEncodingException {
        //https://www.dropbox.com/preview/Appointments/Listing%20Officials.xlsx?role=personal
        assertNotNull(dropboxConnector.getFile("/Appointments/Listing Officials.xlsx"));
        assertNull(dropboxConnector.getFile("/désignations/Listing-----AN.xlsx"));
        String xlsPath = "/design_sms/2017-2018/10-oct%202017/app%2025-29.xlsx";
        String decoded = URLDecoder.decode(xlsPath, "UTF-8");
        assertEquals("/design_sms/2017-2018/10-oct 2017/app 25-29.xlsx", decoded);
        //assertNotNull(dropboxConnector.getFile(decoded));
    }
}
*/
