package com.openkey.sdk.api.response.session_cred;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KabaSdkParams {

    @SerializedName("kabaMobileAppTechUser")
    @Expose
    private String kabaMobileAppTechUser;
    @SerializedName("kabaMobileTechPassword")
    @Expose
    private String kabaMobileTechPassword;

    public String getKabaMobileAppTechUser() {
        return kabaMobileAppTechUser;
    }

    public void setKabaMobileAppTechUser(String kabaMobileAppTechUser) {
        this.kabaMobileAppTechUser = kabaMobileAppTechUser;
    }

    public String getKabaMobileTechPassword() {
        return kabaMobileTechPassword;
    }

    public void setKabaMobileTechPassword(String kabaMobileTechPassword) {
        this.kabaMobileTechPassword = kabaMobileTechPassword;
    }

}