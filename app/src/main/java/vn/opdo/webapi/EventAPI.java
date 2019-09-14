package vn.opdo.webapi;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import vn.opdo.model.Event;
import vn.opdo.model.Student;

public interface EventAPI {
    @GET("/api/event")
    public void getEvent(Callback<List<Event>> callback);

    @GET("/api/event/{id}")
    public void getListStudentEvent(@Path("id") Integer id, Callback<List<Student>> callback);

    @POST("/api/event/{id}")
    public void addStudentEvent(@Path("id") Integer id, @Body Student student, Callback<Student> callback);

    @DELETE("/api/event/{id}")
    public void removeStudentEvent(@Path("id") Integer id, @Query("IdStudent") Integer IdStudent, Callback<Boolean> callback);
}
