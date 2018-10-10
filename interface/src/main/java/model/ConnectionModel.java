package model;

public class ConnectionModel {
    private String url;
    private String user;
    private String pass;
    private String DBName;
    private String port;

    public ConnectionModel(String url, String port, String user, String pass, String DBName) {
        this.url = url;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.DBName = DBName;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getDBName() {
        return DBName;
    }

    public String getPort() {
        return port;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return getDBName();
    }
}
