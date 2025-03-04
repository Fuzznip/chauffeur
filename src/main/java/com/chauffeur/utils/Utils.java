package com.chauffeur.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Text;
import java.util.concurrent.CompletableFuture;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

@Slf4j
@UtilityClass
public class Utils {
    public static String sanitize(String str)
    {
        if (str == null || str.isEmpty()) return "";
        return Text.removeTags(str.replace("<br>", "\n")).replace('\u00A0', ' ').trim();
    }

    public static <T> CompletableFuture<T> readJson(@NotNull OkHttpClient httpClient, @NotNull Gson gson, @NotNull String url, @NotNull TypeToken<T> type) {
        return readUrl(httpClient, url, reader -> gson.fromJson(reader, type.getType()));
    }

    public static <T> CompletableFuture<T> readUrl(@NotNull OkHttpClient httpClient, @NotNull String url, @NotNull Function<Reader, T> transformer) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assert response.body() != null;
                try (Reader reader = response.body().charStream()) {
                    future.complete(transformer.apply(reader));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                } finally {
                    response.close();
                }
            }
        });
        return future;
    }
}
