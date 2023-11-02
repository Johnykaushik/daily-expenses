package com.kharche.model;

public class Category {
    public int id;
    public String categoryName;
    public int associatedSpent;

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", associatedSpent=" + associatedSpent +
                '}';
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getAssociatedSpent() {
        return associatedSpent;
    }

    public void setAssociatedSpent(int associatedSpent) {
        this.associatedSpent = associatedSpent;
    }
}
