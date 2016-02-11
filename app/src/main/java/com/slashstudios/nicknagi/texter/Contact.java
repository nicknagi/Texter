package com.slashstudios.nicknagi.texter;

import com.orm.SugarApp;

/**
 * Created by nicknagi on 09-07-2015.
 */
public class Contact extends SugarApp {
    String[] _name = new String[250];
    String[] _phone_number = new String[250];
    String Message = "";
    //private variables
    private int SendYEAR = 0, SendMONTH = 0, SendDAY = 0, SendHOUR = 0, SendMINUTE = 0;

    // Empty constructor
    public Contact() {

    }


    // constructor
    public Contact(String name[], String _phone_number[]) {
        this._name = name;
        this._phone_number = _phone_number;
    }
    // getting ID


    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }


    // getting name
    public String[] getName() {
        return this._name;
    }

    // setting name
    public void setName(String name) {
        int i;
        for (i = 0; i < 250; i++) {
            if (_name[i] == null) {
                _name[i] = name;
                break;
            }
        }
    }

    public void setName(String[] name) {
        this._name = name;
    }

    public String getNameArrayValue(int position) {
        return this._name[position];
    }

    public void setNameArrayValue(String name, int position) {
        this._name[position] = name;
    }

    // getting phone number
    public String[] getPhoneNumber() {
        return this._phone_number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number) {
        int i;
        for (i = 0; i < 250; i++) {
            if (_phone_number[i] == null) {
                _phone_number[i] = phone_number;
                break;
            }
        }
    }

    public void setPhoneNumber(String[] phone_number) {
        this._phone_number = phone_number;
    }

    public String getPhoneNumberArrayValue(int position) {
        return this._phone_number[position];
    }

    public void setPhoneNumberArrayValue(String phone_number, int position) {
        this._phone_number[position] = phone_number;
    }

    public int getSendYEAR() {
        return SendYEAR;
    }

    public void setSendYEAR(int sendYEAR) {
        SendYEAR = sendYEAR;
    }

    public int getSendMINUTE() {
        return SendMINUTE;
    }

    public void setSendMINUTE(int sendMINUTE) {
        SendMINUTE = sendMINUTE;
    }

    public int getSendHOUR() {
        return SendHOUR;
    }

    public void setSendHOUR(int sendHOUR) {
        SendHOUR = sendHOUR;
    }

    public int getSendDAY() {
        return SendDAY;
    }

    public void setSendDAY(int sendDAY) {
        SendDAY = sendDAY;
    }

    public int getSendMONTH() {
        return SendMONTH;
    }

    public void setSendMONTH(int sendMONTH) {
        SendMONTH = sendMONTH;
    }

    public String[] getFilledNames() {
        String[] names = new String[250];
        int i;
        for (i = 0; i < 250; i++) {
            if (_name[i] != null) {
                names[i] = _name[i];
            }
        }
        return names;
    }

    public int getNumberOfNames() {
        int i, counter = 0;
        for (i = 0; i < 250; i++) {
            if (_name[i] != null) {
                counter++;
            }
        }
        return counter;
    }

    public int getNumberOfNumbers() {
        int i, counter = 0;
        for (i = 0; i < 250; i++) {
            if (_phone_number[i] != null) {
                counter++;
            }
        }
        return counter;
    }

    public String[] getFilledPhoneNumbers() {
        String[] numbers = new String[250];
        int i;
        for (i = 0; i < 250; i++) {
            if (_phone_number[i] != null) {
                numbers[i] = _phone_number[i];
            }
        }
        return numbers;
    }

    public void clearClass() {
        SendYEAR = 0;
        SendMONTH = 0;
        SendDAY = 0;
        SendHOUR = 0;
        SendMINUTE = 0;
        Message = "";
        int i;
        for (i = 0; i < 250; i++) {
            _name[i] = null;
            _phone_number[i] = null;
        }

    }
}
