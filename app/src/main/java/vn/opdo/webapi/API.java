package vn.opdo.webapi;

public class API {
    private static final String URL = "http://hutechitevent.somee.com";
    private retrofit.RestAdapter restAdapter;
    private LoginAPI loginAPI;
    private EventAPI eventAPI;

    public API()
    {
        restAdapter = new retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .build();

        loginAPI = restAdapter.create(LoginAPI.class);
        eventAPI = restAdapter.create(EventAPI.class);
    }

    public EventAPI getEventAPI()
    {
        return eventAPI;
    }

    public LoginAPI getLoginAPI()
    {
        return loginAPI;
    }

}
