package com.ng.techhouse.tinggqr.model;

/**
 * Created by rabiu on 27/02/2017.
 */

public class CardPojo {

    String CVV2,CardPan,ExpiryDate,CardType,PhoneNumber,Id,ExtraData;

    private boolean selected;
    public CardPojo(String id, String extraData, String phoneNumber, String cardType) {
        Id = id;
        ExtraData = extraData;
        PhoneNumber = phoneNumber;
        CardType = cardType;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCVV2() {
        return CVV2;
    }

    public void setCVV2(String CVV2) {
        this.CVV2 = CVV2;
    }

    public String getCardPan() {
        return CardPan;
    }

    public void setCardPan(String cardPan) {
        CardPan = cardPan;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getCardType() {
        return CardType;
    }

    public void setCardType(String cardType) {
        CardType = cardType;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getExtraData() {
        return ExtraData;
    }

    public void setExtraData(String extraData) {
        ExtraData = extraData;
    }
}
