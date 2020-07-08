package Serlizables;

import java.io.Serializable;

public enum ServerMassages implements Serializable {
    LOGIN_SUCCESSFUL, USERNAME_NOT_FOUND, WRONG_PASSWORD, USERNAME_ALREADY_EXISTS, EMAIL_ALREADY_EXISTS, SIGN_UP_SUCCESSFUL, LOGOUT_SUCCESSFUL, UNKNOWN_ERROR, ALREADY_LOGED_IN, WRONG_QUESTION_OR_ANSWER, EMAIL_NOT_FOUND, RECOVER_PASSWORD_SUCCESSFUL;

    @Override
    public String toString() {
        String s = super.toString();
        s = s.toLowerCase();
        s = s.replace('_', ' ');
        return s;
    }
}
