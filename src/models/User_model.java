package models;

public class User_model {
    public static String userName;
    public static String userID;
    public static String timezone;
    public static String language;
    public static int age;



    public static void update_usrModel(String LoginORout, String[] anUsrArray){
        if(LoginORout.equals("in")){
            userID = anUsrArray[0];
            userName = anUsrArray[1];
        }else {
            userID = null;
            userName = null;
        }
    }
}
