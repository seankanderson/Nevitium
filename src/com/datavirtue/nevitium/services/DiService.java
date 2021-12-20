package com.datavirtue.nevitium.services;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 * @author SeanAnderson
 */
public class DiService {
    public static Injector getInjector() {
        return Guice.createInjector(new GuiceBindingModule());
    }
}
