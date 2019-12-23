package sdk.chat.micro.types;

import android.content.Context;

import java.util.HashMap;

import sdk.chat.micro.firestore.Keys;

public class ContactType extends BaseType {

    /**
     * They have full access rights, can add and remove admins
     */
    public static String Contact = "contact";

    public ContactType(String type) {
        super(type);
    }

    public ContactType(BaseType type) {
        super(type);
    }

    public static ContactType contact() {
        return new ContactType(Contact);
    }

    public HashMap<String, Object> data () {
        HashMap<String, Object> data = new HashMap<>();
        data.put(Keys.Type, get());
        return data;
    }

}
