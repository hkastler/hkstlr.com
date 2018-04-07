/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.hkstlr.app.control;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author henry.kastler
 */
public class DateFormatter {

    
    /**
     * yyyy MMM dd HH:mm
     */
    public static final DateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm");
    
    /**
     * yyyy
     */
    public static final DateFormat yearFormat = new SimpleDateFormat("yyyy");
    
    /**
     * MM
     */
    public static final DateFormat monthFormat = new SimpleDateFormat("MM");
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    public static final DateFormat dayFormat = new SimpleDateFormat("d");
    public static final DateFormat format8chars = new SimpleDateFormat("YYYYMMDD");
    public static final DateFormat yearMonthFormat = new SimpleDateFormat("yyyyMMM");
    
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     * thanks to https://github.com/jarrodhroberson/Stack-Overflow/blob/master/src/main/java/com/stackoverflow/Q2597083.java
     */
    public static final DateFormat jsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    
    public static final DateFormat datePickerFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    
    public static DateFormat localeDefaultDateFormat(Locale currentLocale) {
        return DateFormat.getDateInstance(DateFormat.SHORT ,currentLocale);
    }
    
    
    private DateFormatter() {
        //constructor
    }

}