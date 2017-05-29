package downloadbooster.mj.com.retrofitdownloadbooster;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mjohnston on 2017-05-28.
 */

public class DownloadBoosterTests {

    @Test
    public void TestRangeString() {

        DownloadBooster db = new DownloadBooster();
        int chunkSize = 10;

        assertEquals(db.GetRangeHeaderValue(0, chunkSize), "bytes=0-9");
        assertEquals(db.GetRangeHeaderValue(1, chunkSize), "bytes=10-19");
        assertEquals(db.GetRangeHeaderValue(2, chunkSize), "bytes=20-29");
        assertEquals(db.GetRangeHeaderValue(3, chunkSize), "bytes=30-39");
        assertEquals(db.GetRangeHeaderValue(4, chunkSize), "bytes=40-49");

    }
}
