package nz.ac.aucklanduni.eyeatlas.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Condition implements Serializable {

    private Integer id;
    private String title;
    private String description;
    private Category category;
    private Set<Tag> tags = new HashSet<Tag>();
    private Integer imageWidth;
    private Integer imageHeight;


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }
}