package com.ayush.triperati.store;


public interface CredentialStore {

    String[] read();

    void write(String[] response);

    void clearCredentials();
}
