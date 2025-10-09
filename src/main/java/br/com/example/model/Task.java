package br.com.example.model;

public class Task {

    private String id;
    private String description;

    public Task() {
    }

    public Task(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toJson(){
        return "{\"id\":\""+id+"\",\"description\":\""+description+"\"}";
    }
}
