package downloadbooster.mj.com.retrofitdownloadbooster;

import android.util.Log;
import android.util.Patterns;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

/**
 * Created by mjohnston on 2017-05-28.
 */

public class DownloadBooster {

    private final String TAG = "DOWNLOAD_BOOSTER";
    private int numberOfParts, partSize;
    Retrofit retrofit;
    byte[] fileBytes;
    int responses = 0;
    private String fullURL;
    private DownloadCallBack callBack;
    private static final String RANGE_HEADER = "Content-Range";

    public  DownloadBooster(String URL, int numberOfParts, int partSize) {
        this.numberOfParts = numberOfParts;
        this.partSize = partSize;
        this.fullURL = URL;

        retrofit = new Retrofit.Builder()
                .baseUrl("http://fakeurl/") //Weird limitation of retrofit that this needs set here.
                .build();
    }

    public boolean validateInput() {
        if(partSize > 0 && numberOfParts > 0 && Patterns.WEB_URL.matcher(fullURL).matches()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void startDownload(DownloadCallBack callBack) {

        if(!validateInput()) {
            callBack.DownloadError("Invalid Input");
            return;
        }

        this.callBack = callBack;

        Downloader downloader = retrofit.create(Downloader.class);

        for (int i=0; i < numberOfParts; i++) {

            Call<ResponseBody> call = downloader.GetFilePart(GetRangeHeaderValue(i, partSize), fullURL);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful() && response.code() == HttpURLConnection.HTTP_PARTIAL) {
                        AddFileContent(response);
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
        void DownloadComplete(byte[] file);
        void DownloadError(String Error);
    }


    public interface Downloader {
        @GET
        Call<ResponseBody> GetFilePart(@Header("Range") String contentRange, @Url String fileURL);
    }

    public static Integer GetFileSizeFromHeader(okhttp3.Headers headers) {
        int i;

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

    public synchronized void AddFileContent(Response<ResponseBody> response) {

        responses++;
        int start = GetStartFromHeader(response.headers());
        int end = GetEndFromHeader(response.headers());

        if(fileBytes == null) {
            int totalFileSize = GetFileSizeFromHeader(response.headers());
            int requestSize = numberOfParts * partSize;
            int actualSize = totalFileSize < requestSize ? totalFileSize : requestSize;

            fileBytes = new byte[actualSize];
        }
        int i=0;
        while(start < end) {

            try {
                fileBytes[start] = response.body().bytes()[i];
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            start++;
            i++;
        }
        if(responses >= numberOfParts) {
            callBack.DownloadComplete(fileBytes);
        }


    }


    public static int GetStartFromHeader(Headers headers) {
        int i;
        String range = headers.get(RANGE_HEADER);
        String s = range.substring(range.indexOf(" ") + 1, range.lastIndexOf("-"));

        i = Integer.valueOf(s);

        return i;
    }

    public static int GetEndFromHeader(Headers headers) {

        int i;
        String range = headers.get(RANGE_HEADER);
        String s = range.substring(range.indexOf("-") + 1, range.lastIndexOf("/"));

        i = Integer.valueOf(s);

        return i;
    }
}

