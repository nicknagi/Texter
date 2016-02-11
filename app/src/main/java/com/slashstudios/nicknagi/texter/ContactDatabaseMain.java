package com.slashstudios.nicknagi.texter;

import com.orm.SugarRecord;

/**
 * Created by nicknagi on 19-07-2015.
 */
public class ContactDatabaseMain extends SugarRecord<ContactDatabaseMain> {
    String Name;
    String Number;
    Long Eventid;

    public ContactDatabaseMain(){
    }

    public ContactDatabaseMain(Long Eventid, String name, String number){
        this.Name = name;
        this.Number = number;
        this.Eventid=Eventid;
    }
}
