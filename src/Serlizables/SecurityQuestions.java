package Serlizables;

import java.io.Serializable;

public enum SecurityQuestions implements Serializable {
    WHAT_WAS_THE_LAST_NAME_OF_YOUR_THIRD_GRADE_TEACHER, WHAT_WAS_THE_NAME_OF_YOUR_SECOND_PET, WHO_WAS_YOUR_CHILDHOOD_HERO, WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND;


    @Override
    public String toString() {
        String s = super.toString();
        String s1 = s.substring(0, 1).toUpperCase();
        String massageCapitalized = s1 + s.substring(1).toLowerCase() + "?";
        return massageCapitalized.replace("_", " ");
    }


}
