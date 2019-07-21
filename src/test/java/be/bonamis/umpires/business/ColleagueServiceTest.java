package be.bonamis.umpires.business;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ColleagueServiceTest {

    @Test
    public void clean() {
        String before = "abcé, ";
        assertThat(ColleagueService.clean(before)).isEqualTo("abce");
    }
}