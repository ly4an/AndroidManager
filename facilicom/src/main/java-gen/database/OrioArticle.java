package database;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "ORIO_ARTICLE".
 */
@Entity
public class OrioArticle {

    @Id
    private Long id;
    private String UID;
    private String Name;

    @Generated
    public OrioArticle() {
    }

    public OrioArticle(Long id) {
        this.id = id;
    }

    @Generated
    public OrioArticle(Long id, String UID, String Name) {
        this.id = id;
        this.UID = UID;
        this.Name = Name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

}
