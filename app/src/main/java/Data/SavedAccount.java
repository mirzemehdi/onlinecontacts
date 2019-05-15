package Data;

import android.content.Context;
import android.content.SharedPreferences;

public class SavedAccount {


    private SharedPreferences savedAccount;

    public SavedAccount(Context context) {
        savedAccount=context.getSharedPreferences("savedAccount",Context.MODE_PRIVATE);

    }
    public void removeCurrentAccount(){

        setUsername("");
        setPassword("");
    }

    public String getUsername() {
        return savedAccount.getString("username",null);

    }

    public void setUsername(String username) {
        savedAccount.edit().putString("username",username).commit();
    }

    public String getPassword() {
        return savedAccount.getString("password",null);
    }

    public void setPassword(String password) {
        savedAccount.edit().putString("password",password).commit();
    }






}
