package downloadbooster.mj.com.retrofitdownloadbooster;

import android.util.Log;

import java.io.File;
import java.net.HttpURLConnection;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

/**
 * Created by mjohnston on 2017-05-28.
 */

public class DownloadBooster {

    private final String TAG = "DOWNLOAD_BOOSTER";
    private int numberOfParts, partSize;
    private String baseURL, fileName;
    Retrofit retrofit;
    private int fileLength;

    private String fullURL = baseURL + fileName;

    public void DownloadBooster(String baseURL,String fileName, int numberOfParts, int partSize) {
        this.baseURL = baseURL;
        this.fileName = fileName;
        this.numberOfParts = numberOfParts;
        this.partSize = partSize;

        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .build();
    }

    public void StartDownload(DownloadCallBack callBack) {

        Downloader downloader = retrofit.create(Downloader.class);

        for (int i=0; i < numberOfParts; i++) {

            Call<ResponseBody> call = downloader.GetFilePart(GetRangeHeaderValue(i, partSize), fullURL);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful() && response.code() == HttpURLConnection.HTTP_PARTIAL) {
                        //fileLength = GetFileSizeFromHeader(response.headers());
                        Log.d(TAG, "Happy Days");
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "error");
                }
            });
        }

    }



    public interface DownloadCallBack {
        public void Complete(File file);
    }


    public interface Downloader {
        @GET
        Call<ResponseBody> GetFilePart(@Header("Range") String contentRange, @Url String URL);
    }

    public static Integer GetFileSizeFromHeader(okhttp3.Headers headers) {
        int i;
        String RANGE_HEADER = "Content-Range";
        String range = headers.get(RANGE_HEADER);
        String s = range.substring(range.lastIndexOf("/") + 1);

        i = Integer.valueOf(s);

        return i;

    }

    public static String GetRangeHeaderValue(int numberOfParts, int sizeOfPart) {
        int start,end;

        start = numberOfParts * sizeOfPart;
        end = start + (sizeOfPart - 1);

        return String.format("bytes=%d-%d", start, end);
    }

    public void AddfileContent(Response<ResponseBody> response) {

        int start = GetStartFromHeader(response.headers());

    }


    public static int GetStartFromHeader(Headers headers) {
        int i;
        String RANGE_HEADER = "Content-Range";
        String range = headers.get(RANGE_HEADER);
        String s = range.substring(range.indexOf(" ") + 1, range.lastIndexOf("/"));

        i = Integer.valueOf(s);

        return i;
    }

    public static int GetEndFromHeader(Headers headers) {

        int i;
        String RANGE_HEADER = "Content-Range";
        String range = headers.get(RANGE_HEADER);
        String s = range.substring(range.indexOf(" ") + 1, range.lastIndexOf("/"));

        i = Integer.valueOf(s);

        return i;

    }


}

