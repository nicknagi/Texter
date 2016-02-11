package com.slashstudios.nicknagi.texter;

import com.orm.SugarRecord;

/**
 * Created by nicknagi on 19-07-2015.
 */
public class ContactDatabaseReference  extends SugarRecord<ContactDatabaseReference> {
    String Message;
    Long Time;
    Long Eventid;
    int Alarmid;

    public ContactDatabaseReference(){
    }

    public ContactDatabaseReference(Long Eventid, String Message, Long Time, int Alarmid){
        this.Message = Message;
        this.Time = Time;
        this.Eventid=Eventid;
        this.Alarmid=Alarmid;
    }
}
