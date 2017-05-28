package downloadbooster.mj.com.retrofitdownloadbooster;

import org.junit.Test;

import static downloadbooster.mj.com.retrofitdownloadbooster.DownloadBooster.GetRangeHeaderValue;
import static org.junit.Assert.assertEquals;

/**
 * Created by mjohnston on 2017-05-28.
 */

public class DownloadBoosterTests {

    @Test
    public void TestRangeString() {

        Integer chunkSize = 10;

        assertEquals(GetRangeHeaderValue(0, chunkSize), "bytes=0-9");
        assertEquals(GetRangeHeaderValue(1, chunkSize), "bytes=10-19");
        assertEquals(GetRangeHeaderValue(2, chunkSize), "bytes=20-29");
        assertEquals(GetRangeHeaderValue(3, chunkSize), "bytes=30-39");
        assertEquals(GetRangeHeaderValue(4, chunkSize), "bytes=40-49");

    }


}
