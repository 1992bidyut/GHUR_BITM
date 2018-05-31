package bdnath.lictproject.info.ghur.FireBasePojoClass;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String fullName;
    private String Email;
    private String gender;
    private String dOb;
    private String city;
    private String Country;
    private String profileImageUrl;

    public UserInfo() {
    }

    public UserInfo(String fullName, String email, String gender, String dOb, String city, String country, String profileImageUrl) {
        this.fullName = fullName;
        this.Email = email;
        this.gender = gender;
        this.dOb = dOb;
        this.city = city;
        Country = country;
        this.profileImageUrl = profileImageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getdOb() {
        return dOb;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCountry() {
        return Country;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setdOb(String dOb) {
        this.dOb = dOb;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
