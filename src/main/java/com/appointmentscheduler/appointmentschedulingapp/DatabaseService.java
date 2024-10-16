package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.*;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private final static DatabaseService INSTANCE = new DatabaseService();

    private DatabaseService() {
    }

    public static DatabaseService getInstance() {
        return INSTANCE;
    }

    /**
     * Connects to the MySQL Database and returns a PreparedStatement.
     *
     * @param query
     * @return
     * @throws SQLException
     */
    public PreparedStatement getStatement(String query) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule",
                "sqlUser", "Passw0rd!");
        return conn.prepareStatement(query);
    }

    public int generateID(String table, String idColumn) throws SQLException {
        PreparedStatement preparedStatement = getStatement("SELECT MAX(" + idColumn + ") FROM `" + table + "`");
        int maxId = 0;
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            maxId = rs.getInt(1);
        }
        return maxId + 1;
    }


    public static Timestamp localToUtcTimestamp(LocalDateTime localDatetime) {
        return Timestamp.valueOf(localDatetime.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }

    public static LocalDateTime utcToLocalDatetime(Timestamp utcTimestamp) {
        return utcTimestamp.toInstant().atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Timestamp utcTimestampToLocalTimestamp(Timestamp utcTimestamp) {
        if (utcTimestamp == null) {
            return null;
        }
        return Timestamp.valueOf(utcToLocalDatetime(utcTimestamp));
    }

    public Metadata getMetadata(ResultSet rs) throws SQLException {
        Metadata metadata = new Metadata();

        Timestamp createDate = rs.getTimestamp("Create_Date");
        if (createDate != null) {
            metadata.setCreateDate(Utility.localOffset(
                    Utility.timestampToLocalDT(createDate.toString()),
                    Utility.getUTCOffset(),
                    Utility.getSystemOffset()));
        }

        Timestamp lastUpdate = rs.getTimestamp("Last_Update");
        if (lastUpdate != null) {
            metadata.setLastUpdate(Utility.localOffset(
                    Utility.timestampToLocalDT(lastUpdate.toString()),
                    Utility.getUTCOffset(),
                    Utility.getSystemOffset()));
        }


        metadata.setCreatedBy(rs.getString("Created_By"));
        metadata.setLastUpdatedBy(rs.getString("Last_Updated_By"));
        return metadata;
    }

    /*********************************************************************************************
     * Country
     *********************************************************************************************/
    /**
     * Retrieves all countries from the database and returns them in a List.
     *
     * @return
     * @throws SQLException
     */
    public List<Country> getCountries() throws SQLException {
        Country country;
        List<Country> countries = new ArrayList<>();
        PreparedStatement preparedStatement = getStatement("SELECT * FROM Countries");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            country = new Country();
            country.setCountryId(rs.getInt("Country_ID"));
            country.setCountry(rs.getString("Country"));
            country.setMetadata(getMetadata(rs));
            countries.add(country);
        }
        return countries;
    }

    public Country getCountry(int countryId) throws SQLException {
        Country country = null;
        PreparedStatement preparedStatement = getStatement("SELECT * FROM Countries WHERE Country_ID = ?");
        preparedStatement.setInt(1, countryId);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            country = new Country();
            country.setCountryId(rs.getInt("Country_ID"));
            country.setCountry(rs.getString("Country"));
            country.setMetadata(getMetadata(rs));
        }
        return country;
    }

    /*********************************************************************************************
     * Division
     *********************************************************************************************/

    /**
     * Retrieves all divisions from the database and returns them in a List.
     *
     * @return
     * @throws SQLException
     */
    public List<Division> getDivisions() throws SQLException {
        Division division;
        List<Division> divisions = new ArrayList<>();
        PreparedStatement preparedStatement = getStatement("SELECT * FROM `first_level_divisions`");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            division = new Division();
            division.setDivisionId(rs.getInt("Division_ID"));
            division.setDivision(rs.getString("Division"));
            division.setMetadata(getMetadata(rs));
            division.setCountryId(rs.getInt("Country_ID"));
            divisions.add(division);
        }
        return divisions;
    }

    public List<Division> getDivisions(int countryId) throws SQLException {
        Division division;
        List<Division> divisions = new ArrayList<>();
        PreparedStatement preparedStatement = getStatement("SELECT * FROM `first_level_divisions` WHERE Country_ID = ?");
        preparedStatement.setInt(1, countryId);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            division = new Division();
            division.setDivisionId(rs.getInt("Division_ID"));
            division.setDivision(rs.getString("Division"));
            division.setMetadata(getMetadata(rs));
            division.setCountryId(rs.getInt("Country_ID"));
            divisions.add(division);
        }
        return divisions;
    }

    public Division getDivision(int divisionId) throws SQLException {
        Division division = null;
        PreparedStatement preparedStatement = getStatement("SELECT * FROM `first_level_divisions` WHERE Division_ID = ?");
        preparedStatement.setInt(1, divisionId);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            division = new Division();
            division.setDivisionId(rs.getInt("Division_ID"));
            division.setDivision(rs.getString("Division"));
            division.setMetadata(getMetadata(rs));
            division.setCountryId(rs.getInt("Country_ID"));
        }
        return division;
    }

    /*********************************************************************************************
     * Customer
     *********************************************************************************************/


    /**
     * Retrieves all customers from the database and returns them in a List.
     *
     * @return
     * @throws SQLException
     */
    public List<Customer> getCustomers() throws SQLException {
        Customer customer;
        List<Customer> customers = new ArrayList<>();
        PreparedStatement preparedStatement = getStatement("SELECT * FROM Customers");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            customer = new Customer();
            customer.setId(rs.getInt("Customer_ID"));
            customer.setName(rs.getString("Customer_Name"));
            customer.setAddress(rs.getString("Address"));
            customer.setPostalCode(rs.getString("Postal_Code"));
            customer.setPhone(rs.getString("Phone"));
            customer.setMetadata(getMetadata(rs));
            customer.setDivisionId(rs.getInt("Division_ID"));
            customers.add(customer);
        }
        return customers;
    }

    public Customer getCustomer(int customerId) throws SQLException {
        Customer customer = null;
        PreparedStatement preparedStatement = getStatement("SELECT * FROM Customers WHERE Customer_ID = ?");
        preparedStatement.setInt(1, customerId);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            customer = new Customer();
            customer.setId(rs.getInt("Customer_ID"));
            customer.setName(rs.getString("Customer_Name"));
            customer.setAddress(rs.getString("Address"));
            customer.setPostalCode(rs.getString("Postal_Code"));
            customer.setPhone(rs.getString("Phone"));
            customer.setMetadata(getMetadata(rs));
            customer.setDivisionId(rs.getInt("Division_ID"));
        }
        return customer;
    }

    public boolean saveCustomer(Customer newCustomer, String query, boolean newRecord) throws SQLException {
        PreparedStatement preparedStatement = null;
        preparedStatement = getStatement(query);
        preparedStatement.setString(1, newCustomer.getName());
        preparedStatement.setString(2, newCustomer.getAddress());
        preparedStatement.setString(3, newCustomer.getPostalCode());
        preparedStatement.setString(4, newCustomer.getPhone());
        if (newRecord) {
            preparedStatement.setTimestamp(5, Timestamp.valueOf(newCustomer.getDateCreatedAsOffsetDT().
                    toLocalDateTime()));
            preparedStatement.setString(6, newCustomer.getCreatedBy());
        } else {
            preparedStatement.setTimestamp(5, Timestamp.valueOf(newCustomer.getLastUpdatesAsOffsetDT().
                    toLocalDateTime()));
            preparedStatement.setString(6, newCustomer.getLastUpdatedBy());
        }
        preparedStatement.setInt(7, newCustomer.getDivision().getDivisionId());
        preparedStatement.setInt(8, newCustomer.getId());

        return preparedStatement.executeUpdate() > 0;

    }

    public boolean deleteCustomer(Customer customer) throws SQLException, SQLIntegrityConstraintViolationException {
        PreparedStatement preparedStatement = null;
        preparedStatement = getStatement("DELETE FROM Customers WHERE Customer_ID = ?");
        preparedStatement.setInt(1, customer.getId());
        return preparedStatement.executeUpdate() > 0;
    }


    /*********************************************************************************************
     * User
     *********************************************************************************************/

    /**
     * Retrieves all users from the database and returns them in a List.
     *
     * @return
     * @throws SQLException
     */
    public List<User> getUsers() throws SQLException {
        User user;
        List<User> users = new ArrayList<>();
        PreparedStatement preparedStatement = getStatement("SELECT * FROM Users");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            user = new User();
            user.setId(rs.getInt("User_ID"));
            user.setUsername(rs.getString("User_Name"));
            user.setMetadata(getMetadata(rs));
            users.add(user);
        }
        return users;
    }

    /**
     * Return user with given username and password.
     *
     * @param userId
     * @return
     * @throws SQLException
     */
    public User getUser(int userId) throws SQLException {
        User user = null;
        PreparedStatement preparedStatement = null;
        preparedStatement = getStatement("SELECT * FROM users WHERE User_ID = ?");
        preparedStatement.setInt(1, userId);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            user = new User();
            user.setId(rs.getInt("User_ID"));
            user.setUsername(rs.getString("User_Name"));
            user.setPassword(rs.getString("Password"));
            user.setMetadata(getMetadata(rs));

        }
        return user;
    }

    public User getUser(String username, String password) throws SQLException {

        User user = null;
        PreparedStatement preparedStatement = null;
        preparedStatement = getStatement("SELECT * FROM users WHERE User_Name = ?");
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            if (rs.getString("Password").equals(password)) {
                user = new User();
                user.setId(rs.getInt("User_ID"));
                user.setUsername(rs.getString("User_Name"));
                user.setMetadata(getMetadata(rs));
            }
        }
        return user;
    }

    /*********************************************************************************************
     * Contact
     *********************************************************************************************/

    /**
     * Retrieves all contacts from the database and returns them in a List.
     *
     * @return
     * @throws SQLException
     */
    public List<Contact> getContacts() throws SQLException {
        Contact contact;
        List<Contact> contacts = new ArrayList<>();
        PreparedStatement preparedStatement = getStatement("SELECT * FROM Contacts");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            contact = new Contact();
            contact.setContactId(rs.getInt("Contact_ID"));
            contact.setName(rs.getString("Contact_Name"));
            contact.setEmail(rs.getString("Email"));
            contacts.add(contact);
        }
        return contacts;
    }

    public Contact getContact(int contactId) throws SQLException {
        Contact contact = null;
        PreparedStatement preparedStatement = getStatement("SELECT * FROM Contacts Where Contact_ID = ?");
        preparedStatement.setInt(1, contactId);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            contact = new Contact();
            contact.setContactId(rs.getInt("Contact_ID"));
            contact.setName(rs.getString("Contact_Name"));
            contact.setEmail(rs.getString("Email"));
        }
        return contact;
    }


    /*********************************************************************************************
     * Appointment
     *********************************************************************************************/



    /**
     * Retrieves all appointments from the database and returns them in a List.
     *
     * @return
     * @throws SQLException
     */

    public List<Appointment> getAppointments() throws SQLException {
        PreparedStatement preparedStatement = getStatement("SELECT * FROM Appointments");
        ResultSet rs = preparedStatement.executeQuery();

        return getAppointments(rs);
    }

    /**
     * Retrieves all appointments from the database and returns them in a List.
     *
     * @return
     * @throws SQLException
     */

    public List<Appointment> getAppointments(ResultSet rs) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        Appointment appointment;

        while (rs.next()) {
            appointment = new Appointment();
            appointment.setAppointmentId(rs.getInt("Appointment_ID"));
            appointment.setTitle(rs.getString("Title"));
            appointment.setDescription(rs.getString("Description"));
            appointment.setLocation(rs.getString("Location"));
            appointment.setType(rs.getString("Type"));

            OffsetDateTime startOffsetDT = Utility.localOffset(
                    Utility.timestampToLocalDT(rs.getTimestamp("Start").toString()),
                    Utility.getUTCOffset(),
                    Utility.getSystemOffset());

            appointment.setStart(startOffsetDT);

            OffsetDateTime endOffsetDT = Utility.localOffset(
                    Utility.timestampToLocalDT(rs.getTimestamp("End").toString()),
                    Utility.getUTCOffset(),
                    Utility.getSystemOffset());

            appointment.setEnd(endOffsetDT);

            appointment.setMetadata(getMetadata(rs));
            appointment.setCustomerId(rs.getInt("Customer_ID"));
            appointment.setUserId(rs.getInt("User_ID"));
            appointment.setContactId(rs.getInt("Contact_ID"));

            appointments.add(appointment);
        }
        return appointments;
    }



    public boolean saveAppointment(Appointment appointment, String query, boolean newRecord) throws SQLException {
        PreparedStatement preparedStatement = getStatement(query);
        preparedStatement.setString(1, appointment.getTitle());
        preparedStatement.setString(2, appointment.getDescription());
        preparedStatement.setString(3, appointment.getLocation());
        preparedStatement.setString(4, appointment.getType());
        preparedStatement.setObject(5, appointment.getStartAsOffsetDT().toLocalDateTime());
        preparedStatement.setObject(6, appointment.getEndAsOffsetDT().toLocalDateTime());
        if (newRecord) {
            preparedStatement.setObject(7, appointment.getDateCreatedAsOffsetDT().toLocalDateTime());
            preparedStatement.setString(8, appointment.getCreatedBy());
        } else {
            preparedStatement.setObject(7, appointment.getLastUpdatesAsOffsetDT().toLocalDateTime());
            preparedStatement.setString(8, appointment.getLastUpdatedBy());
        }
        preparedStatement.setInt(9, appointment.getCustomerId());
        preparedStatement.setInt(10, appointment.getUserId());
        preparedStatement.setInt(11, appointment.getContactId());
        preparedStatement.setInt(12, appointment.getAppointmentId());

        return preparedStatement.executeUpdate() > 0;
    }

    protected boolean isOverlapping(OffsetDateTime startDatetime, OffsetDateTime endDatetime) throws SQLException {
        String query = "SELECT Appointment_ID FROM appointments WHERE ";

        // startDatetime <= db.Start AND endDatetime >= db.End
        query += " ( ? <= Start AND ? >= End ) OR ";

        // startDatetime >= db.Start AND startDatetime <= db.End
        query += " ( ? >= Start AND ? <= End ) OR ";

        // endDatetime >= db.Start AND endDatetime <= db.End
        query += " ( ? >= Start AND ? <= End ) ";

        PreparedStatement preparedStatement = null;
        preparedStatement = getStatement(query);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(startDatetime.toLocalDateTime()));
        preparedStatement.setTimestamp(2, Timestamp.valueOf(endDatetime.toLocalDateTime()));

        preparedStatement.setTimestamp(3, Timestamp.valueOf(startDatetime.toLocalDateTime()));
        preparedStatement.setTimestamp(4, Timestamp.valueOf(startDatetime.toLocalDateTime()));

        preparedStatement.setTimestamp(5, Timestamp.valueOf(endDatetime.toLocalDateTime()));
        preparedStatement.setTimestamp(6, Timestamp.valueOf(endDatetime.toLocalDateTime()));
        ResultSet rs = preparedStatement.executeQuery();
        return rs.next();
    }

    /**
     * @param appointment
     * @return
     * @throws SQLException
     */

    public boolean deleteAppointment(Appointment appointment) throws SQLException {
        PreparedStatement preparedStatement = null;
        preparedStatement = getStatement("DELETE FROM appointments WHERE Appointment_ID = ?");
        preparedStatement.setInt(1, appointment.getAppointmentId());
        int updated = preparedStatement.executeUpdate();
        return updated > 0;
    }

    /**
     * ----------------------------------------------------------------------------------------------------
     * Reports and stats
     * ----------------------------------------------------------------------------------------------------
     */

    /**
     * Returns a list of objects of the AppointmentCount class, which stores the total number of customer appointments
     * for specific type in a particular month.
     *
     * @return  a list of AppointmentCount objects
     * @throws SQLException when there is an error reading the database.
     */
    public List<AppointmentCount> countAppointmentsByTypeMonth() throws SQLException {
        List<AppointmentCount> appointmentCounts = new ArrayList<>();

        AppointmentCount appointmentCount;

        PreparedStatement preparedStatement = getStatement("SELECT MONTHNAME(Start) AS AppointmentMonth, " +
                " Type, Count(Customer_ID) As TotalAppointments FROM Appointments" +
                " GROUP BY AppointmentMonth, Type");

        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            appointmentCount = new AppointmentCount(rs.getString("AppointmentMonth"),
                    rs.getString("Type"), rs.getInt("TotalAppointments"));

            appointmentCounts.add(appointmentCount);
        }
        return appointmentCounts;
    }

    public List<CustomerCount> countDivisionCustomers() throws SQLException {
        List<CustomerCount> customersCounts = new ArrayList<>();

        CustomerCount customerCount;

        PreparedStatement preparedStatement = getStatement("SELECT Division, Count(a.Customer_ID) As TotalCustomers" +
                " FROM Appointments a INNER JOIN customers c ON a.Customer_ID = c.Customer_ID " +
                " INNER JOIN `first_level_divisions` d ON c.Division_ID = d.Division_ID  " +
                " GROUP BY Division");

        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            customerCount = new CustomerCount(rs.getString("Division"), rs.getInt("TotalCustomers"));

            customersCounts.add(customerCount);
        }
        return customersCounts;
    }



}
