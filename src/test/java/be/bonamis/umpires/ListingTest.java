package be.bonamis.umpires;

import be.bonamis.umpires.arbh.Listing;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class ListingTest {

    @Test
    public void getAllUmpires() throws Exception {
        URL root = ListingTest.class.getProtectionDomain().getCodeSource().getLocation();
        URL propertiesFileUrl = new URL(root, "Listing AN.xlsx");

        InputStream inputStream = propertiesFileUrl.openStream();
        Listing listing = new Listing(inputStream);

        assertThat(listing.getUmpires())
                .hasSize(45)
                .extracting("name", "first", "eMail", "mobile")
                .contains(
                        tuple("Bonamis", "Florent", "florent@bonamis.be", "0475/219 945"),
                        tuple("Somers", "Jean-Paul", "jean-paul.somers@pb-media.eu", "0475/957 011"));
    }
}