package FirstTab;

public class parsePhone {

    public parsePhone() {}

    public static String parse(String phone) {
        String phonenumber = "";
        if (phone != null) {
            phonenumber = phone.replaceAll("-", "");
            if (phonenumber.length() == 10) {
                phonenumber = phonenumber.substring(0, 3) + "-"
                        + phonenumber.substring(3, 6) + "-"
                        + phonenumber.substring(6);
            } else if (phonenumber.length() > 8) {
                phonenumber = phonenumber.substring(0, 3) + "-"
                        + phonenumber.substring(3, 7) + "-"
                        + phonenumber.substring(7);
            }
        }

        return phonenumber;
    }

    public static String merge(String phone){
        return phone.replaceAll("-", "");
    }
}
