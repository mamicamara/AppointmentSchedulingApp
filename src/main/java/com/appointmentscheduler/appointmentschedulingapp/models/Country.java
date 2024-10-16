package com.appointmentscheduler.appointmentschedulingapp.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Country extends Metadata {
        private int countryId;
        private String country;

        public int getCountryId() {
                return countryId;
        }

        public void setCountryId(int countryId) {
                this.countryId = countryId;
        }

        public String getCountry() {
                return country;
        }

        public void setCountry(String country) {
                this.country = country;
        }

}
