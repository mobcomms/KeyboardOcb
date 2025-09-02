package com.enliple.keyboard.common;

import java.util.ArrayList;

public class SearchModel {
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<Messages> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Messages> messages) {
        this.messages = messages;
    }

    private int totalCount;
    private ArrayList<Messages> messages;



    public static class Messages {
        public int getIndexSeq() {
            return indexSeq;
        }

        public void setIndexSeq(int indexSeq) {
            this.indexSeq = indexSeq;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public OhSaraMarket getOhsaraMarket() {
            return ohsaraMarket;
        }

        public void setOhsaraMarket(OhSaraMarket ohsaraMarket) {
            this.ohsaraMarket = ohsaraMarket;
        }

        private int indexSeq;
        private String category;
        private OhSaraMarket ohsaraMarket;
    }

    public static class OhSaraMarket {
        private String id;
        private String productName;
        private String imageUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public long getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(long originalPrice) {
            this.originalPrice = originalPrice;
        }

        public String getDisCountText() {
            return disCountText;
        }

        public void setDisCountText(String disCountText) {
            this.disCountText = disCountText;
        }

        public String getSaveText() {
            return saveText;
        }

        public void setSaveText(String saveText) {
            this.saveText = saveText;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }

        private long price;
        private long originalPrice;
        private String disCountText;
        private String saveText;
        private String linkUrl;
    }
}
