package vn.opdo.webapi;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import vn.opdo.model.Login;

public interface LoginAPI {
    @POST("/api/login")
    public void checkLogin(@Body Login login, Callback<String> callback);
}
