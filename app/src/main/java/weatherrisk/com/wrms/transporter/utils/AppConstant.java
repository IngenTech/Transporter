package weatherrisk.com.wrms.transporter.utils;

import java.util.Locale;

/**
 * Created by Admin on 21-04-2017.
 */
public class AppConstant {

    public static class Constants {


        public static final String INDIVIDUAL_CUSTOMER = "0";
        public static final String CORPORATE_CUSTOMER = "1";

        public static final String PENDING_ORDER_TYPE = "2";
        public static final String CONFIRM_ORDER_TYPE = "1";

        public static final String INVOICE_UPLOAD = "1";
        public static final String DECLARATION_UPLOAD = "2";
        public static final String ROAD_PERMIT_UPLOAD = "3";

    }

    public static class API {


        public static final String ADDRESS_OF_LAT_LNG_API = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language=" + Locale.getDefault().getCountry();

    }
}
