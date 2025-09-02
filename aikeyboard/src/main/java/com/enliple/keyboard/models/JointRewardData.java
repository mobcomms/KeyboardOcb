package com.enliple.keyboard.models;

public class JointRewardData {
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getClick_url() {
        return click_url;
    }

    public void setClick_url(String click_url) {
        this.click_url = click_url;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AdChoices getAdChoices() {
        return adChoices;
    }

    public void setAdChoices(AdChoices adChoices) {
        this.adChoices = adChoices;
    }

    public String getClick_key_name() {
        return click_key_name;
    }

    public void setClick_key_name(String click_key_name) {
        this.click_key_name = click_key_name;
    }

    public String getEprs_key_name() {
        return eprs_key_name;
    }

    public void setEprs_key_name(String eprs_key_name) {
        this.eprs_key_name = eprs_key_name;
    }

    private String productId;
    private String image;
    private String title;
    private String description;
    private String price;
    private String click_url;
    private String logo_url;
    private String kind;
    private String type;
    private AdChoices adChoices;
    private String click_key_name;
    private String eprs_key_name;
}
