package nz.ac.aucklanduni.eyeatlas.model;

import java.util.HashSet;
import java.util.Set;

public class Condition {

    private String title;
    private String description;
    private Category category;
    private Set<Tag> tags = new HashSet<Tag>();

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
}