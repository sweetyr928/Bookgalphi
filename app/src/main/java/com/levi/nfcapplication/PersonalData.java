package com.levi.nfcapplication;

public class PersonalData {
    private String member_id;
    private String member_name;
    private String member_country;

    public String getMember_id() {
        return member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public String getMember_country() {
        return member_country;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public void setMember_country(String member_country) {
        this.member_country = member_country;
    }
}